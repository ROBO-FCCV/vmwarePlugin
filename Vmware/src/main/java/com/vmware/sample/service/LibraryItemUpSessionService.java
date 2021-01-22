/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.content.library.item.UpdateSessionModel;

/**
 * Library item update session service
 *
 * @since 2020-10-14
 */
public interface LibraryItemUpSessionService {
    /**
     * Create update session
     *
     * @param vmwareId vmware id
     * @param updateSessionModel update session
     * @return update session id
     */
    String create(String vmwareId, UpdateSessionModel updateSessionModel);

    /**
     * Complete session
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     */

    void complete(String vmwareId, String sessionId);

    /**
     * Delete session
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     */
    void delete(String vmwareId, String sessionId);

    /**
     * Cancel session
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     */
    void cancel(String vmwareId, String sessionId);

    /**
     * Client session fail
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     * @param failedMsg failed message
     */
    void fail(String vmwareId, String sessionId, String failedMsg);

    /**
     * Get up session
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     * @return session
     */
    UpdateSessionModel get(String vmwareId, String sessionId);
}
