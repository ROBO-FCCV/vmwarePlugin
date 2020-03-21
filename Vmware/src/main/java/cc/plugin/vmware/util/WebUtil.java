/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.token.TokenVo;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 返回工具类
 *
 * @since 2019 -10-15
 */
public class WebUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebUtil.class);

    /**
     * 校验Token是否失效
     *
     * @param t the t
     * @return the boolean
     */
    public static boolean checkIfTokenNotExpired(TokenVo t) {
        return System.currentTimeMillis() - t.getCreatedTime() <= Constant.EXPIRED_TIME;
    }

    /**
     * 返回客户端
     *
     * @param response the response
     * @param result the result
     */
    public static void formatResponse(HttpServletResponse response, RestResult result) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(result));
            response.getWriter().flush();
        } catch (IOException e) {
            logger.error("formatResponse IOException");
        }
    }

    /**
     * 获取request
     *
     * @return the request
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取response
     *
     * @return the response
     */
    public static HttpServletResponse getResponse() {
        return ((ServletWebRequest) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * Gets client ip.
     *
     * @param request the request
     * @return the client ip
     */
    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        ipAddress = geIpFromJustOneProxy(request, ipAddress);
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        // "***.***.***.***".length() = 15
        if (ipAddress != null && ipAddress.length() > 15) {
            ipAddress = getIpAddress(ipAddress);
        }
        return ipAddress;
    }

    private static String geIpFromJustOneProxy(HttpServletRequest request, String ipAddress) {
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            ipAddress = getIp(ipAddress);
        }
        return ipAddress;
    }

    private static String getIpAddress(String ipAddress) {
        if (ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }

    private static String getIp(String ip) {
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                logger.error("", e);
            }
            ip = inetAddress.getHostAddress();
        }
        return ip;
    }
}
