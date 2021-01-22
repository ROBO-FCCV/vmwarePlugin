/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.util;

import com.vmware.sample.consts.Constants;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ip address check utils
 *
 * @since 2020-09-14
 */
@Slf4j
@UtilityClass
public class IpAddressCheck {
    private final Pattern IP_PATTERN = Pattern.compile(Constants.IP_RULE);
    private final Pattern IP_V4_PATTERN = Pattern.compile(Constants.IPV4);
    private final int DEFAULT_SHELL_TIMEOUT_TIME = 5;

    /**
     * Check whether the IP address can be pinged.
     *
     * @param ip ip address
     * @return boolean
     */
    public boolean isReachable(String ip) {
        Matcher matcher = IP_PATTERN.matcher(ip);
        if (!matcher.matches()) {
            log.error("Ip is error.");
            return false;
        }
        Matcher ipVersion4 = IP_V4_PATTERN.matcher(ip);
        List<String> commands = new ArrayList<>();
        commands.add(ipVersion4.matches() ? "ping" : "ping6");
        if (SystemUtils.IS_OS_WINDOWS) {
            commands.add(ip);
            commands.add("-n");
            commands.add("3");
            commands.add("-w");
            commands.add("5");
        } else if (SystemUtils.IS_OS_LINUX) {
            commands.add(ip);
            commands.add("-c");
            commands.add("3");
            commands.add("-W");
            commands.add("5");
        } else {
            log.info("This os  wasn't support.");
            return false;
        }
        return getPingResult(commands);
    }

    private boolean getPingResult(List<String> commands) {
        try {
            Process process = new ProcessBuilder(commands).start();
            boolean waitForDone = process.waitFor(DEFAULT_SHELL_TIMEOUT_TIME, TimeUnit.SECONDS);
            if (waitForDone) {
                String err = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
                String out = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
                if (StringUtils.isNotEmpty(err)) {
                    log.error("Process error {}", StringUtils.strip(err));
                }
                return StringUtils.containsIgnoreCase(StringUtils.strip(out), "TTL");
            }
        } catch (IOException | InterruptedException ioException) {
            log.error("IOException or InterruptedException", SensitiveExceptionUtils.hideSensitiveInfo(ioException));
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
