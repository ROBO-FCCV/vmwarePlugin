
/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * The type Runtime util.
 *
 * @since 2019 -09-19
 */
public class RuntimeUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeUtil.class);
    private static final int DEFAULT_SHELL_TIMEOUT_TIME = 5;

    /**
     * 执行命令，返回结果
     *
     * @param cmdArr the cmd arr
     * @return the string
     */
    public static String executeCommand(String[] cmdArr) {
        try {
            Process process = new ProcessBuilder(cmdArr).start();
            boolean waitForDone = process.waitFor(DEFAULT_SHELL_TIMEOUT_TIME, TimeUnit.SECONDS);
            if (waitForDone) {
                String err = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
                String out = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
                if (StringUtils.isNotEmpty(err)) {
                    LOGGER.error("Process error {}", StringUtils.strip(err));
                }
                return StringUtils.strip(out);
            }
        } catch (IOException | InterruptedException exception) {
            LOGGER.error("IOException or InterruptedException", exception);
        }
        return null;
    }
}
