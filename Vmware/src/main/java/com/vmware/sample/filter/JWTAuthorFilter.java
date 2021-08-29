/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.filter;

import com.vmware.sample.service.impl.VMwareUserDetailServiceImpl;
import com.vmware.sample.util.SensitiveExceptionUtils;

import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.util.Strings;
import org.pac4j.core.profile.jwt.JwtClaims;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Jwt filter
 *
 * @since 2020-09-14
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class JWTAuthorFilter extends OncePerRequestFilter {
    private final VMwareUserDetailServiceImpl vMwareUserDetailService;
    private final JwtAuthenticator jwtAuthenticator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (!StringUtils.isEmpty(jwt)) {
                Map<String, Object> stringObjectMap = jwtAuthenticator.validateTokenAndGetClaims(jwt);
                UserDetails userDetails = vMwareUserDetailService.loadUserByUsername(
                    MapUtils.getString(stringObjectMap, JwtClaims.SUBJECT));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication:", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return Strings.EMPTY;
    }
}
