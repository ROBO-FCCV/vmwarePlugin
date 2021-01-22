/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

/**
 * Pac4j config
 *
 * @since 2020-09-14
 */
@Configuration
public class Pac4jConfig {
    private static final byte[] SALT;

    static {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        SALT = bytes;
    }

    /**
     * JwtAuthenticator
     *
     * @return JwtAuthenticator
     */
    @Bean
    protected JwtAuthenticator jwtAuthenticator() {
        JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration(SALT));
        jwtAuthenticator.addEncryptionConfiguration(new SecretEncryptionConfiguration(SALT));
        return jwtAuthenticator;
    }

    /**
     * JwtGenerator
     *
     * @return JwtGenerator
     */
    @Bean
    protected JwtGenerator<JwtProfile> jwtGenerator() {
        return new JwtGenerator<>(new SecretSignatureConfiguration(SALT), new SecretEncryptionConfiguration(SALT));
    }
}
