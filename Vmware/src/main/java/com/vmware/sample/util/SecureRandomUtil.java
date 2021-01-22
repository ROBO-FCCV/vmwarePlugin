/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 随机数生成器
 *
 * @since 2020-12-28
 */
@Slf4j
@UtilityClass
public class SecureRandomUtil {
    /**
     * 生成随机数种子
     *
     * @return 随机数
     * @throws NoSuchAlgorithmException 没有这个算法异常
     */
    public static SecureRandom generate() throws NoSuchAlgorithmException {
        SecureRandom source = SecureRandom.getInstanceStrong();
        BlockCipher cipher = new AESEngine();
        int cipherLen = 256;
        byte[] nonce = new byte[cipherLen / 8];
        source.nextBytes(nonce);
        return (new SP800SecureRandomBuilder(source, true)).buildCTR(cipher, cipherLen, nonce, false);
    }
}
