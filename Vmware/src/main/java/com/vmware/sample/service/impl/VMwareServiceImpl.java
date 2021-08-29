/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.config.KmcProperties;
import com.vmware.sample.config.VMwareProperties;
import com.vmware.sample.enums.Operation;
import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.VMware;
import com.vmware.sample.service.VMwareService;
import com.vmware.sample.util.KmcUtils;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

/**
 * VMware service
 *
 * @since 2020-09-27
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VMwareServiceImpl implements VMwareService {
    private static final TypeReference<Map<String, VMwareProperties>> TYPE
        = new TypeReference<Map<String, VMwareProperties>>() {};
    private final VMwareProperties vMwareProperties;
    private final ConcurrentHashMap<String, VMwareAPI> stringVMwareAPIMap;
    private final ConcurrentHashMap<String, VMwareSDK> stringVMwareSDKMap;
    private final KmcProperties kmcProperties;
    private final ApplicationContext applicationContext;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public String add(VMware vmware) {
        Set<String> collect = stringVMwareAPIMap.values()
            .stream()
            .map(item -> item.getVMware().getIp())
            .collect(Collectors.toSet());
        if (collect.contains(vmware.getIp())) {
            log.warn("The ip {} was exist.", vmware.getIp());
            throw new PluginException(RestCodeEnum.IP_EXIST);
        }
        VMwareAPI vMwareAPI = new VMwareAPI(vmware);
        VMwareSDK vMwareSDK = new VMwareSDK(vmware);
        vmware.setId(UUID.randomUUID().toString().replace("-", ""));
        boolean login = vMwareAPI.login();
        if (login) {
            stringVMwareAPIMap.put(vmware.getId(), vMwareAPI);
            stringVMwareSDKMap.put(vmware.getId(), vMwareSDK);
            // 异步的写yaml文件
            asyncWriteToFile(vmware, Operation.ADD);
            return vmware.getId();
        } else {
            throw new PluginException(RestCodeEnum.VMWARE_LOGIN_FAILED);
        }
    }

    private synchronized void asyncWriteToFile(VMware vmware, Operation operation) {
        try {
            File file = ResourceUtils.getFile("classpath:vmware.yml");
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            objectMapper.registerModule(new Jdk8Module());
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            Map<String, VMwareProperties> vMwareYaml = objectMapper.readValue(new FileInputStream(file), TYPE);
            Map<String, VMware> configs = vMwareYaml.get("vmware").getConfigs();
            VMware copy = new VMware();
            BeanUtils.copyProperties(vmware, copy);
            if (kmcProperties.isEnabled()) {
                KmcUtils kmcUtils = applicationContext.getBean(KmcUtils.class);
                copy.setPassword(kmcUtils.encrypt(String.valueOf(copy.getPassword())).toCharArray());
            }
            switch (operation) {
                case ADD:
                    configs.put(copy.getId(), copy);
                    break;
                case DEL:
                    configs.remove(copy.getId());
                    break;
                case MODIFY:
                    VMware vMware = configs.get(copy.getId());
                    BeanUtils.copyProperties(copy, vMware);
                    break;
                default:
                    log.warn("Unsupported operation");
            }
            objectMapper.writeValue(file, vMwareYaml);
        } catch (IOException e) {
            log.error("The vmware.yml properties not found.", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
    }

    @Override
    public VMware getVMware(String vmwareId) {
        return vMwareProperties.getConfigs().get(vmwareId);
    }

    @Override
    public List<VMware> list() {
        return stringVMwareAPIMap.values().stream().map(VMwareAPI::getVMware).collect(Collectors.toList());
    }

    @Override
    public String del(String vmwareId) {
        VMwareAPI vMwareAPI = stringVMwareAPIMap.get(vmwareId);
        VMwareSDK vMwareSDK = stringVMwareSDKMap.get(vmwareId);
        if (vMwareAPI == null || vMwareSDK == null) {
            log.warn("The VMware id {} wasn't exist", vmwareId);
            throw new PluginException(RestCodeEnum.VMWARE_NOT_EXISTED);
        }
        VMwareAPI removeVmware = stringVMwareAPIMap.remove(vmwareId);
        VMwareSDK remove = stringVMwareSDKMap.remove(vmwareId);
        log.info("Logout without check vmware result.");
        // 异步线程去登出，不管它的返回结果，直接改文件
        taskExecutor.execute(() -> {
            removeVmware.logout();
            remove.logout();
        });
        asyncWriteToFile(removeVmware.getVMware(), Operation.DEL);
        return vmwareId;
    }

    @Override
    public String modify(String vmwareId, @NotNull VMware vMware) {
        VMwareAPI vMwareTestLogin = new VMwareAPI(vMware);
        boolean login = vMwareTestLogin.login();
        if (login) {
            VMwareAPI vMwareAPI = stringVMwareAPIMap.get(vmwareId);
            VMwareSDK vMwareSDK = stringVMwareSDKMap.get(vmwareId);
            if (vMwareAPI == null || vMwareSDK == null) {
                log.warn("The VMware id {} wasn't exist", vmwareId);
                throw new PluginException(RestCodeEnum.VMWARE_NOT_EXISTED);
            }
            VMwareAPI newVMwareAPI = new VMwareAPI(vMware);
            VMwareSDK newVMwareSDK = new VMwareSDK(vMware);
            stringVMwareAPIMap.put(vmwareId, newVMwareAPI);
            stringVMwareSDKMap.put(vmwareId, newVMwareSDK);
            vMware.setId(vmwareId);
            asyncWriteToFile(vMware, Operation.MODIFY);
            return vmwareId;
        } else {
            throw new PluginException(RestCodeEnum.VMWARE_LOGIN_FAILED);
        }
    }

    @Override
    public VMware get(String vmwareId) {
        Optional<VMwareAPI> any = stringVMwareAPIMap.values()
            .stream()
            .filter(it -> it.getVMware().getId().equalsIgnoreCase(vmwareId))
            .findAny();
        if (any.isPresent()) {
            return any.get().getVMware();
        } else {
            throw new PluginException(RestCodeEnum.VMWARE_NOT_EXISTED);
        }
    }
}
