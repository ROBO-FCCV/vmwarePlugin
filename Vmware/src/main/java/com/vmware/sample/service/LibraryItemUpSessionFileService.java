/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.content.library.item.updatesession.FileTypes;

import java.util.List;

/**
 * Library item update session file service
 *
 * @since 2020-10-14
 */
public interface LibraryItemUpSessionFileService {
    /**
     * Add file
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     * @param addSpec add spec
     * @return file info
     */
    FileTypes.Info add(String vmwareId, String sessionId, FileTypes.AddSpec addSpec);

    /**
     * Get file
     *
     * @param vmwareId vmware id
     * @param updateSessionFileId update session file id
     * @param fileName fileName
     * @return file
     */
    FileTypes.Info get(String vmwareId, String updateSessionFileId, String fileName);

    /**
     * Query file upload error
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     * @return file upload error
     */
    FileTypes.ValidationResult validate(String vmwareId, String sessionId);

    /**
     * List the files
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     * @return files
     */
    List<FileTypes.Info> list(String vmwareId, String sessionId);
}
