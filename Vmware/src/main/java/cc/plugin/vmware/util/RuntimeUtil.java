
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Runtime util.
 *
 * @since 2019 -09-19
 */
public class RuntimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeUtil.class);

    /**
     * 执行命令，返回结果
     *
     * @param cmdArr the cmd arr
     * @return the string
     * @throws IOException the io exception
     */
    public static String executeCommand(String[] cmdArr) throws IOException {
        StringBuffer result = new StringBuffer();
        BufferedReader reader = null;
        InputStream ins = null;
        Process ps = null;
        try {
            ps = Executors.newSingleThreadExecutor().submit(new Callable<Process>() {
                @Override
                public Process call() throws IOException {
                    return exec(cmdArr);
                }
            }).get();

            // 用输入输出流来截取结果
            ins = ps.getInputStream();
            reader = new BufferedReader(new InputStreamReader(ins));
            LineIterator lineItr = new LineIterator(reader);
            while (lineItr.hasNext()) {
                result.append(lineItr.next());
            }
            boolean exitVal = getProcessResult(ps);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException in runCommandGetResult,cause:{}", e);
        } catch (Exception e) {
            LOGGER.error("ExecutionException in runCommandGetResult,cause: {}", e);
        } finally {
            StreamUtil.close(reader);
            StreamUtil.close(ins);
            if (ps != null) {
                ps.destroyForcibly();
            }
        }

        return result.toString();
    }

    private static boolean getProcessResult(Process p) {
        boolean result;
        try {
            result = Executors.newSingleThreadExecutor().submit(() -> {
                if (p.waitFor(1000, TimeUnit.MINUTES)) {
                    int code = p.exitValue();
                    return code == 0;
                }
                return false;
            }).get();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException:{}", e);
            result = false;
        } catch (ExecutionException e) {
            LOGGER.error("ExecutionException:{}", e);
            result = false;
        }
        return result;
    }

    private static Process exec(String[] cmdArr) throws IOException {
        return Runtime.getRuntime().exec(cmdArr);
    }

}
