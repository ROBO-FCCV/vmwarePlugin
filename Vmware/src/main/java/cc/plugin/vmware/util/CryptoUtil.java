/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import cc.plugin.vmware.exception.ServerException;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * The type Crypto util.
 *
 * @since 2019 -10-15
 */
public class CryptoUtil {

    /**
     * Generate strong password hash string.
     *
     * @param password the password
     * @return the string
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeySpecException the invalid key spec exception
     */
    public static String generateStrongPasswordHash(String password)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        byte[] salt = generateSalt();
        byte[] hash = generateHashedPassword(iterations, password.toCharArray(), salt, 512);
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    /**
     * Generate hashed password byte [ ].
     *
     * @param iterations the iterations
     * @param chars the chars
     * @param salt the salt
     * @param length the length
     * @return the byte [ ]
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeySpecException the invalid key spec exception
     */
    public static byte[] generateHashedPassword(int iterations, char[] chars, byte[] salt, int length)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, length);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * To hex string.
     *
     * @param array the array
     * @return the string
     */
    public static String toHex(byte[] array) {
        BigInteger binary = new BigInteger(1, array);
        String hex = binary.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        return paddingLength > 0 ? String.format(Locale.ENGLISH, "%0" + paddingLength + "d", 0) + hex : hex;
    }

    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * Validate password boolean.
     *
     * @param userInputPassword the user input password
     * @param hashedPassword the hashed password
     * @return the boolean
     * @throws ServerException the server exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws InvalidKeySpecException the invalid key spec exception
     */
    public static boolean validatePassword(String userInputPassword, String hashedPassword)
        throws ServerException, NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = hashedPassword.split(":");
        if (parts.length != 3) {
            throw new ServerException(
                "hashed password : " + hashedPassword + " format is not correct , please check it in database");
        }
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
        byte[] hashValue = generateHashedPassword(iterations, userInputPassword.toCharArray(), salt, hash.length * 8);
        int diff = hash.length ^ hashValue.length;
        for (int i = 0; i < hash.length && i < hashValue.length; i++) {
            diff |= hash[i] ^ hashValue[i];
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex) {
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return result;
    }

}
