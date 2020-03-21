/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import cc.plugin.vmware.exception.ServerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * 登录账户名、密码encoder
 *
 * @since 2019 -09-19
 */
public class Encoder implements PasswordEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(Encoder.class);

    @Override
    public String encode(CharSequence password) {
        if (password != null) {
            try {
                return CryptoUtil.generateStrongPasswordHash(password.toString());
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                LOGGER.error("password encode error " + e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodePassword) {
        LOGGER.info("************into Encoder.matches");
        String rawPassStr = "";
        if (rawPassword != null) {
            rawPassStr = rawPassword.toString();
        }
        if (rawPassStr.length() > 0 && encodePassword != null && encodePassword.length() > 0) {
            try {
                return CryptoUtil.validatePassword(rawPassStr, encodePassword);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | ServerException e) {
                LOGGER.error("failed validate password with error : " + e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

}
