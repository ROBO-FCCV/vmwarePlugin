/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import java.security.SecureRandom;

/**
 * 功能描述
 *
 * @since 2019 -09-26
 */
public class StringRandom {

    private static final int DEFAULT_LENGTH = 8;

    /**
     * Gets string random.
     *
     * @param length the length
     * @return the string random
     */
    public static String getStringRandom(int length) {
        StringBuffer result = new StringBuffer();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length - 1; i++) {
            String charOrNumber = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equalsIgnoreCase(charOrNumber)) {
                int rm = random.nextInt(2) % 2 == 0 ? 65 : 97;
                result.append((char) (random.nextInt(26) + rm));
            } else {
                result.append(String.valueOf(random.nextInt(10)));
            }
        }
        String[] potArry = "~!@#$%^_+?{}[]|:".split("");
        String randomStr = potArry[random.nextInt(potArry.length)];
        result.append(randomStr);
        return result.toString();
    }

    /**
     * Gets string random.
     *
     * @return the string random
     */
    public static String getStringRandom() {
        return getStringRandom(DEFAULT_LENGTH);
    }
}
