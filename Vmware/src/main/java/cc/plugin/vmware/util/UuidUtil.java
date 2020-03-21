/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @since 2019 -10-18
 */
public final class UuidUtil {
    /**
     * MDC中traceId使用的key
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 生成业务id
     *
     * @return 业务id string
     */
    public static String generateBusinessId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成trace id
     *
     * @return trace id
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
