/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import cc.plugin.vmware.constant.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * The type Cipher.
 *
 * @since 2019 -10-15
 */
@Component
public class Cipher {
    private static final Logger logger = LoggerFactory.getLogger(Cipher.class);

    @Value("${kmcScriptLocation}")
    private String rootLocation;

    /**
     * 加密
     *
     * @param encryptionPassword the encryption password
     * @return the string
     */
    public String encrypt(String encryptionPassword) {
        String exe = "python";
        String[] cmdArr = new String[] {
            exe, rootLocation + Constant.encryptLocation, encryptionPassword
        };
        try {
            return RuntimeUtil.executeCommand(cmdArr);
        } catch (IOException var4) {
            logger.error("decryptZookeeperPassWord failed,cause:{}", var4);
            return null;
        }
    }

    /**
     * 解密
     *
     * @param decryptionPassword the decryption password
     * @return the string
     */
    public String decrypt(String decryptionPassword) {
        String exe = "python";
        String[] cmdArr = new String[] {
            exe, rootLocation + Constant.decryptLocation, decryptionPassword
        };

        try {
            return RuntimeUtil.executeCommand(cmdArr);
        } catch (IOException var4) {
            logger.error("decryptZookeeperPassWord failed,cause:{}", var4);
            return null;
        }
    }
}
