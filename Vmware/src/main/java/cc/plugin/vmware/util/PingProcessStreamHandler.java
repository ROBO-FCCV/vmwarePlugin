/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import cc.plugin.vmware.constant.Constant;

import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * The type Ping process stream handler.
 *
 * @since 2019 -10-15
 */
public class PingProcessStreamHandler implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(PingProcessStreamHandler.class);

    private InputStream inpuStream;

    /**
     * Instantiates a new Ping process stream handler.
     *
     * @param inpuStream the inpu stream
     */
    public PingProcessStreamHandler(InputStream inpuStream) {
        this.inpuStream = inpuStream;
    }

    @Override
    public Boolean call() throws Exception {
        logger.info("ProcessStreamHandler Call enter...");
        if (inpuStream == null) {
            return false;
        }

        int connectedCount = 0;
        try (BufferedReader input = new BufferedReader(new InputStreamReader(inpuStream, Constant.CHARSET_UTF8))) {
            LineIterator lineIter = new LineIterator(input);
            while (lineIter.hasNext()) {
                connectedCount += getCheckResult(lineIter.nextLine());
                if (connectedCount != 0) {
                    break;
                }
            }
        } catch (IOException e1) {
            logger.error("ping ip line exception", e1);
        }
        boolean isConnect = connectedCount != 0;
        logger.info("connectedCount :　{}", connectedCount);
        logger.info("isConnect : {}", isConnect);
        return isConnect;
    }

    /**
     * 若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
     */
    private int getCheckResult(String pingOut) {
        if (pingOut == null) {
            return 0;
        } else {
            if ((pingOut.contains("MS") || pingOut.contains("ms")) && (pingOut.contains("TTL") || pingOut.contains(
                "ttl"))) {
                return 1;
            }
        }

        return 0;
    }
}
