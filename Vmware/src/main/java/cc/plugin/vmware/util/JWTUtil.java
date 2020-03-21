/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.profile.JwtGenerator;
import org.pac4j.jwt.profile.JwtProfile;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Jwt util.
 *
 * @since 2019 -09-19
 */
public class JWTUtil {

    private static byte[] MAC_SECRET = new byte[32];

    private JWTUtil() {

    }

    private static Date getExpireDate30Min() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE) + 30);
        return calendar.getTime();
    }

    /**
     * Generate jwt token string.
     *
     * @param data the data
     * @return the string
     */
    public static String generateJwtToken(String data) {
        return generateJwtToken(data, null);
    }

    /**
     * Generate jwt token string.
     *
     * @param data the data
     * @param expire the expire
     * @return the string
     */
    public static String generateJwtToken(String data, Date expire) {
        if (MAC_SECRET == null) {
            SecureRandom random = new SecureRandom();
            MAC_SECRET = new byte[32];
            random.nextBytes(MAC_SECRET);
        }

        final JwtGenerator<JwtProfile> generator = new JwtGenerator<>(new SecretSignatureConfiguration(MAC_SECRET),
            new SecretEncryptionConfiguration(MAC_SECRET));
        final Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.SUBJECT, data);
        claims.put(JwtClaims.EXPIRATION_TIME, expire == null ? getExpireDate30Min() : expire);
        return generator.generate(claims);
    }

}
