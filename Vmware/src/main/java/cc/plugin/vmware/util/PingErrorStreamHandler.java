/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The type Ping error stream handler.
 *
 * @since 2019 -09-19
 */
public class PingErrorStreamHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PingErrorStreamHandler.class);

    private InputStream inputStream;

    /**
     * Instantiates a new Ping error stream handler.
     *
     * @param inpuStream the inpu stream
     */
    public PingErrorStreamHandler(InputStream inpuStream) {
        this.inputStream = inpuStream;
    }

    @Override
    public void run() {
        logger.info("PingErrorStreamHandler Call enter......");
        InputStreamReader in = null;
        BufferedReader bfr = null;
        try {
            // 读取输入流
            in = new InputStreamReader(inputStream, "utf-8");
            bfr = new BufferedReader(in);
            LineIterator itForInputStream = new LineIterator(bfr);
            StringBuffer resultInputStream = new StringBuffer();
            while (itForInputStream.hasNext()) {
                resultInputStream.append(itForInputStream.next());
            }
            resultInputStream.delete(0, resultInputStream.length());
        } catch (IOException e) {
            logger.error("ProcessLogger run error.......", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("close InputStream : in Error", e);
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("close InputStreamReader : in Error", e);
                }
            }
            if (null != bfr) {
                try {
                    bfr.close();
                } catch (IOException e) {
                    logger.error("close InputStreamReader : bfr Error" + e);
                }
            }
            logger.info("PingErrorStreamHandler Call leave...");
        }
    }
}
