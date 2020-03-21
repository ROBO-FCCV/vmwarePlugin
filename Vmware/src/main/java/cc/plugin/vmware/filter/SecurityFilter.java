/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.filter;

import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.token.TokenCache;
import cc.plugin.vmware.token.TokenVo;
import cc.plugin.vmware.util.Encoder;
import cc.plugin.vmware.util.WebUtil;
import cc.plugin.vmware.util.YamlUtil;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Security filter.
 *
 * @since 2019 -09-19
 */
@Component
public class SecurityFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    /**
     * The Token cache.
     */
    @Autowired
    TokenCache tokenCache;

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
        final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = request.getRequestURI();
        if (StringUtils.equals(url, "/vmware/login")) {
            if (!checkLoginParams(request, response)) {
                return;
            }
        } else if (StringUtils.equals(url, "/vmware/version")) {
            logger.info("Get plugin version");
        } else if (StringUtils.isNotEmpty(url)) {
            if (!checkToken(request, response)) {
                return;
            }
        } else {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            WebUtil.formatResponse(response, new RestResult(ErrorCode.URL_NOT_FOUND_CODE, ErrorCode.URL_NOT_FOUND_MSG));
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private boolean checkLoginParams(HttpServletRequest request, HttpServletResponse response) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            WebUtil.formatResponse(response, new RestResult(ErrorCode.SYSTEM_ERROR_CODE, ErrorCode.SYSTEM_ERROR_MSG));
            return false;
        }
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        Map<String, Object> loginMap = YamlUtil.getYamlMap("login.yml");
        if (MapUtils.isEmpty(loginMap)) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            WebUtil.formatResponse(response, new RestResult(ErrorCode.SYSTEM_ERROR_CODE, ErrorCode.SYSTEM_ERROR_MSG));
            return false;
        }
        String loginUsername = (String) loginMap.get("username");
        String originalPassword = (String) loginMap.get("password");
        Encoder encoder = new Encoder();
        if (!StringUtils.equals(username, loginUsername) || StringUtils.isEmpty(originalPassword) || !encoder.matches(
            password, originalPassword)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            WebUtil.formatResponse(response,
                new RestResult(ErrorCode.USERNAME_PASSWORD_WRONG_CODE, ErrorCode.USERNAME_PASSWORD_WRONG_MSG));
            return false;
        }
        return true;
    }

    private boolean checkToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("X-Auth-Token");
        if (StringUtils.isEmpty(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            WebUtil.formatResponse(response, new RestResult(ErrorCode.UNAUTHORIZED_CODE, ErrorCode.UNAUTHORIZED_MSG));
            return false;
        }
        List<TokenVo> tokens = Optional
            .ofNullable(tokenCache.getTokens(WebUtil.getClientIp(request)))
            .orElseGet(ArrayList::new);
        if (!checkTokenValid(token, tokens)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            WebUtil.formatResponse(response, new RestResult(ErrorCode.UNAUTHORIZED_CODE, ErrorCode.UNAUTHORIZED_MSG));
            return false;
        }
        return true;
    }

    private boolean checkTokenValid(String token, List<TokenVo> tokens) {
        return tokens.stream().anyMatch(to -> to.getToken().equals(token) && WebUtil.checkIfTokenNotExpired(to));
    }

}
