/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.token;

/**
 * 功能描述
 *
 * @since 2019 -09-25
 */
public class TokenVo {
    private String id;
    private String token;
    private long createdTime;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public TokenVo setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     * @return the token
     */
    public TokenVo setToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * Gets created time.
     *
     * @return the created time
     */
    public long getCreatedTime() {
        return createdTime;
    }

    /**
     * Sets created time.
     *
     * @param createdTime the created time
     * @return the created time
     */
    public TokenVo setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
        return this;
    }
}
