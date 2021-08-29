/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.util;

import com.huawei.kmc.common.AppException;
import com.huawei.kmc.crypt.CryptoAPI;
import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Kmc util
 *
 * @since 2020-11-10
 */
@Slf4j
public class KmcUtils {
    public KmcUtils() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(
            ResourceUtils.getFile("classpath:crytotool.properties"))) {
            properties.load(fileInputStream);
            CryptoAPI.getInstance().initialize(properties);
        } catch (IOException | AppException ioException) {
            log.error("Maybe the properties file wasn't exist.Please check the classpath file crytotool.properties");
            throw new PluginException(RestCodeEnum.KMC_ERROR);
        }
    }

    /**
     * Decrypt str
     *
     * @param str string
     * @return decrypt str
     */
    public String decrypt(String str) {
        try {
            Matcher matcher = Pattern.compile("encrypt\\((.*?)\\)").matcher(str);
            if (matcher.matches()) {
                byte[] decrypt = CryptoAPI.getInstance().decrypt(matcher.group(1).getBytes(StandardCharsets.UTF_8));
                return new String(decrypt, StandardCharsets.UTF_8);
            }
            return str;
        } catch (AppException e) {
            log.error("Decrypt str failed.");
            throw new PluginException(RestCodeEnum.KMC_ERROR);
        }
    }

    private String wrap(String str) {
        String prepend = StringUtils.prependIfMissing(str, "encrypt(");
        return StringUtils.appendIfMissing(prepend, ")");
    }

    /**
     * Encrypt str
     *
     * @param str string
     * @return encrypt str
     */
    public String encrypt(String str) {
        try {
            byte[] encrypt = CryptoAPI.getInstance().encrypt(str.getBytes(StandardCharsets.UTF_8));
            String encryptStr = new String(encrypt, StandardCharsets.UTF_8);
            return wrap(encryptStr);
        } catch (AppException e) {
            log.error("Encrypt str failed.str: {}", str);
            throw new PluginException(RestCodeEnum.KMC_ERROR);
        }
    }
}

