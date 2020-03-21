/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.vsphere;

import cc.plugin.vmware.model.vo.response.OvfVo;

import com.alibaba.fastjson.JSONObject;
import com.vmware.content.LibraryModel;
import com.vmware.content.LibraryModel.LibraryType;
import com.vmware.content.library.StorageBacking;
import com.vmware.content.library.StorageBacking.Type;
import com.vmware.content.library.item.UpdateSessionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * The type Import ovf.
 *
 * @since 2019 -09-19
 */
public class ImportOvf extends AbstractSample {
    private static final Logger logger = LoggerFactory.getLogger(ImportOvf.class);

    @Override
    public OvfVo runSample(String[] args) {
        logger.info("ImportOvf enter...");
        int librarySize;
        try {
            librarySize = client.localLibrary().list().size();
        } catch (Exception e) {
            logger.warn("import ovf runSample", e);
            client.login();
            logger.info("Logged in to Content Library API successfully.");
            librarySize = client.localLibrary().list().size();
        }
        logger.info("::::::::::::::get client localLibrary success,size:{}", librarySize);
        if (client.localLibrary().list().size() > 0) {
            client.localLibrary().list().forEach(var -> {
                if (client.localLibrary().get(var).getName().equalsIgnoreCase("my library")) {
                    client.localLibrary().delete(var);
                    logger.info(":::::::::::::::delete my library success");
                }
                ;
            });
        }
        JSONObject imageJsonParamsObj = (JSONObject) JSONObject.parse(args[1]);
        logger.info("Creating a library,name:my library");
        LibraryModel libraryModel = new LibraryModel();
        libraryModel.setName("my library");
        libraryModel.setType(LibraryType.LOCAL);
        StorageBacking libraryBacking = new StorageBacking();
        libraryBacking.setType(Type.DATASTORE);
        libraryBacking.setDatastoreId(imageJsonParamsObj.getString("datastoreId"));
        libraryModel.setStorageBackings(Collections.singletonList(libraryBacking));
        String libraryId = client.localLibrary().create(null, libraryModel);
        String itemId = ClsApiHelper.createItem(client, libraryId, imageJsonParamsObj.getString("itemName"), "ovf");
        logger.info("Created OVF item in the library. ItemId: {}", itemId);
        String sessionId;
        if (imageJsonParamsObj.getString("path") == null) {
            sessionId = ClsApiHelper.importFileFromHttpUriToItem(client, itemId, imageJsonParamsObj.getString("ovfUrl"),
                imageJsonParamsObj.getString("itemOvfName"));
        } else {
            sessionId = ClsApiHelper.uploadSingleFileFromLocalStorageToItem(client, itemId,
                imageJsonParamsObj.getString("itemName"), imageJsonParamsObj.getInteger("diskNum"),
                imageJsonParamsObj.getString("path"));
        }
        ClsApiHelper.itemsFromLibraryLog(client, libraryId);
        UpdateSessionModel updateSession = client.updateSession().get(sessionId);
        String librarySessionId = updateSession.getLibraryItemId();
        logger.info("ImportOvf leave...");
        return new OvfVo().setResourceId(librarySessionId).setSessionId(sessionId);
    }
}
