/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.token;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.util.WebUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述
 *
 * @since 2019 -09-25
 */
@Component
public class TokenCache {

    private static final Logger logger = LoggerFactory.getLogger(TokenCache.class);

    private static Cache<String, List<TokenVo>> tokenCache = CacheBuilder.newBuilder().build();

    /**
     * Deal with token.
     *
     * @param response the response
     * @param ip the ip
     * @param token the token
     * @throws ExecutionException the execution exception
     * @throws CustomException the custom exception
     */
    public void dealWithToken(HttpServletResponse response, String ip, String token)
        throws ExecutionException, CustomException {
        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(token)) {
            return;
        }
        List<TokenVo> tokens = tokenCache.get(ip, () -> Lists.newArrayList(
            new TokenVo().setId(CommonUtil.generateUuid()).setToken(token).setCreatedTime(System.currentTimeMillis())));
        if (tokens.stream().noneMatch(tokenVo -> tokenVo.getToken().equals(token))) {
            if (isTokenNumExceedingMaximum(tokens)) {
                logger.error("Tokens have exceed maximum ");
                throw new CustomException(ErrorCode.MAXIMUM_CONNECTION_CODE, ErrorCode.MAXIMUM_CONNECTION_MSG);
            }
            tokens.add(new TokenVo()
                .setId(CommonUtil.generateUuid())
                .setToken(token)
                .setCreatedTime(System.currentTimeMillis()));
        }
    }

    /**
     * Gets tokens.
     *
     * @param ip the ip
     * @return the tokens
     */
    public List<TokenVo> getTokens(String ip) {
        return tokenCache.getIfPresent(ip);
    }

    private boolean isTokenNumExceedingMaximum(List<TokenVo> tokens) {
        return tokens.stream().filter(WebUtil::checkIfTokenNotExpired).count() >= Constant.TOKEN_MAXIMUM;
    }

    /**
     * Gets valid token.
     *
     * @param ip the ip
     * @return the valid token
     */
    public String getValidToken(String ip) {
        List<TokenVo> tokens = tokenCache.getIfPresent(ip);
        String token = null;
        if (CollectionUtils.isNotEmpty(tokens)) {
            token = tokens
                .stream()
                .filter(WebUtil::checkIfTokenNotExpired)
                .findFirst()
                .orElse(new TokenVo())
                .getToken();
        } else {
            logger.error("There is no valid token in caches");
        }
        return token;
    }

    /**
     * Delete cache.
     */
// 每天1点定时任务清理失效Token
    @Async
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteCache() {
        logger.info("deleteCache starts");
        Map<String, List<TokenVo>> tokenMap = tokenCache.asMap();
        for (Map.Entry<String, List<TokenVo>> entry : tokenMap.entrySet()) {
            String ip = entry.getKey();
            List<TokenVo> tokens = entry.getValue();
            tokens.removeIf(tokenVo -> !WebUtil.checkIfTokenNotExpired(tokenVo));
            if (CollectionUtils.isEmpty(tokens)) {
                tokenMap.remove(ip);
            }
        }
        logger.info("deleteCache ends");
    }
}
