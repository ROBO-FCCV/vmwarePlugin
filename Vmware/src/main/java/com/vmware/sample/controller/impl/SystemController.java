/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.config.VMwareProperties;
import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.ModifyUser;
import com.vmware.sample.model.PluginUser;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.util.CryptoUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.ServerException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Login controller
 *
 * @since 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemController {
    private static final TypeReference<Map<String, String>> TYPE = new TypeReference<Map<String, String>>() {};
    private static final TypeReference<Map<String, VMwareProperties>> VMWARE_PROPERTIES
        = new TypeReference<Map<String, VMwareProperties>>() {};
    private final JwtGenerator<JwtProfile> jwtGenerator;
    private final VMwareProperties vMwareProperties;

    /**
     * Init plugin
     *
     * @param pluginUser pluginUser
     * @return init result
     * @throws InvalidKeySpecException  InvalidKeySpecException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    @PostMapping(value = "/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestResult<String> init(@RequestBody @Valid PluginUser pluginUser)
        throws InvalidKeySpecException, NoSuchAlgorithmException {
        PluginUser user = vMwareProperties.getUser();
        if (user.getPassword() != null) {
            throw new PluginException(RestCodeEnum.PLUGIN_INITIALIZED);
        }
        user.setPassword(CryptoUtil.generateStrongPasswordHash(String.valueOf(pluginUser.getPassword())).toCharArray());
        user.setUsername(pluginUser.getUsername());
        asyncWriteToFile(user);
        return RestResult.success("Init succeeded.");
    }

    /**
     * login
     *
     * @param pluginUser userInfo
     * @return login result
     * @throws ServerException          server exception
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  InvalidKeySpecException
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestResult<String> login(@RequestBody @Valid PluginUser pluginUser)
        throws ServerException, NoSuchAlgorithmException, InvalidKeySpecException {
        PluginUser user = vMwareProperties.getUser();
        if (user.getPassword() == null) {
            throw new PluginException(RestCodeEnum.PLUGIN_NEED_INITIALIZE);
        }
        if (user.getUsername().equalsIgnoreCase(pluginUser.getUsername()) && CryptoUtil.validatePassword(
            String.valueOf(pluginUser.getPassword()), String.valueOf(user.getPassword()))) {
            JwtProfile jwtProfile = new JwtProfile();
            jwtProfile.setId(user.getUsername());
            jwtGenerator.setExpirationTime(
                Date.from(LocalDateTime.now().plus(user.getEmpire()).atZone(ZoneId.systemDefault()).toInstant()));
            String generate = jwtGenerator.generate(jwtProfile);
            return RestResult.success(generate);
        } else {
            return RestResult.fail(RestCodeEnum.UNAUTHORIZED);
        }
    }

    /**
     * update password
     *
     * @param modifyUser 用户修改对象
     * @return result
     * @throws ServerException          server exception
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  InvalidKeySpecException
     */
    @PutMapping(value = "/update-pwd", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestResult<String> updatePassword(@RequestBody @Valid @NotNull ModifyUser modifyUser)
        throws ServerException, NoSuchAlgorithmException, InvalidKeySpecException {
        PluginUser user = vMwareProperties.getUser();
        if (user.getUsername().equalsIgnoreCase(modifyUser.getUsername()) && CryptoUtil.validatePassword(
            String.valueOf(modifyUser.getPassword()), String.valueOf(user.getPassword()))) {
            user.setPassword(
                CryptoUtil.generateStrongPasswordHash(String.valueOf(modifyUser.getNewPassword())).toCharArray());
            vMwareProperties.setUser(user);
            asyncWriteToFile(user);
            jwtGenerator.setExpirationTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            return RestResult.success("Update password succeeded.");
        } else {
            return RestResult.fail(RestCodeEnum.UNAUTHORIZED);
        }
    }

    private synchronized void asyncWriteToFile(PluginUser newPluginUser) {
        try {
            File file = ResourceUtils.getFile("classpath:user.yml");
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            objectMapper.registerModule(new Jdk8Module());
            objectMapper.registerModule(new JavaTimeModule());
            Map<String, VMwareProperties> vMwareYaml = objectMapper.readValue(new FileInputStream(file),
                VMWARE_PROPERTIES);
            VMwareProperties vmware = vMwareYaml.get("vmware");
            PluginUser pluginUser = vmware.getUser();
            BeanUtils.copyProperties(newPluginUser, pluginUser);
            vMwareYaml.put("vmware", vmware);
            objectMapper.writeValue(file, vMwareYaml);
        } catch (IOException e) {
            log.error("The vmware.yml properties not found.");
        }
    }

    /**
     * version
     *
     * @return version
     * @throws IOException exception
     */
    @GetMapping(value = "/version")
    public RestResult<String> version() throws IOException {
        File file = ResourceUtils.getFile("classpath:version.yml");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        Map<String, String> vMwareYaml = objectMapper.readValue(new FileInputStream(file), TYPE);
        return RestResult.success(vMwareYaml.get("version"));
    }
}
