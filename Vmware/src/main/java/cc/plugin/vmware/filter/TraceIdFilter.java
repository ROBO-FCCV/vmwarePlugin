/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.filter;

import cc.plugin.vmware.token.LibraryCache;
import cc.plugin.vmware.util.UuidUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * trace id
 *
 * @since 2019 -11-23
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LibraryCache.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        long beginTime = System.currentTimeMillis();
        try {
            MDC.put(UuidUtil.TRACE_ID, UuidUtil.generateTraceId());
            logger.info("begin request {}", request.getServletPath());
            filterChain.doFilter(request, response);
        } finally {
            logger.info("end request {}, use={}", request.getServletPath(), System.currentTimeMillis() - beginTime);
            MDC.remove(UuidUtil.TRACE_ID);
        }
    }
}
