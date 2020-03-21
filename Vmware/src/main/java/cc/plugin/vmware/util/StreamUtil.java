/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * The type Stream util.
 *
 * @since 2019 -10-15
 */
public class StreamUtil {
    private static final Logger logger = LoggerFactory.getLogger(StreamUtil.class);

    /**
     * Close.
     *
     * @param stream the stream
     */
    public static void close(Closeable stream) {
        try {
            if (null != stream) {
                stream.close();
            }
        } catch (IOException e) {
            logger.error("close stream failed:" + e);
        }
    }

    /**
     * Close.
     *
     * @param c the c
     * @param closes the closes
     */
    public static void close(Closeable c, Closeable... closes) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                logger.error("StreamUtil close failure");
            }
        }
        if (closes == null || closes.length <= 0) {
            return;
        }
        for (Closeable close : closes) {
            if (close != null) {
                try {
                    close.close();
                } catch (IOException e) {
                    logger.error("StreamUtil close failure");
                }
            }
        }
    }
}
