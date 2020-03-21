/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response;

import io.swagger.annotations.ApiModelProperty;

/**
 * Network返回结果
 *
 * @since 2019 -09-10
 */
public class OvfVo {
    @ApiModelProperty(value = "资源标识", example = "2d9a92ee-7997-4aae-b1da-066a8ab2ee9c", required = true)
    private String resourceId;
    @ApiModelProperty(value = "会话模型标识",
        example = "f2875b57-a255-460a-811e-b1446956beb6:f9db8b8e-3d65-4d47-9cb2-a1d5be1be33d", required = true)
    private String sessionId;

    /**
     * Gets resource id.
     *
     * @return the resource id
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets resource id.
     *
     * @param resourceId the resource id
     * @return the resource id
     */
    public OvfVo setResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    /**
     * Gets session id.
     *
     * @return the session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets session id.
     *
     * @param sessionId the session id
     * @return the session id
     */
    public OvfVo setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }
}
