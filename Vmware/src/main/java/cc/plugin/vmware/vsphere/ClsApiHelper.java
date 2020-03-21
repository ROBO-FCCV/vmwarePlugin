/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.vsphere;

import com.vmware.content.Library;
import com.vmware.content.LibraryModel;
import com.vmware.content.library.Item;
import com.vmware.content.library.ItemModel;
import com.vmware.content.library.item.TransferEndpoint;
import com.vmware.content.library.item.UpdateSessionModel;
import com.vmware.content.library.item.updatesession.FileTypes;

import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.util.TaskNodeThreadPoolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

/**
 * The type Cls api helper.
 *
 * @since 2019 -09-19
 */
public class ClsApiHelper {
    private static final Logger logger = LoggerFactory.getLogger(ImportOvf.class);

    /**
     * Gets random client token.
     *
     * @return the random client token
     */
    public static String getRandomClientToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Create item string.
     *
     * @param clsApiClient the cls api client
     * @param libraryId the library id
     * @param itemName the item name
     * @param type the type
     * @return the string
     */
    public static String createItem(ClsApiClient clsApiClient, String libraryId, String itemName, String type) {
        ItemModel itemModel = new ItemModel();
        itemModel.setName(itemName);
        itemModel.setLibraryId(libraryId);
        itemModel.setType(type);
        return clsApiClient.item().create(getRandomClientToken(), itemModel);
    }

    /**
     * Import file from http uri to item string.
     *
     * @param client the client
     * @param itemId the item id
     * @param endpointUri the endpoint uri
     * @param fileName the file name
     * @return the string
     */
    public static String importFileFromHttpUriToItem(ClsApiClient client, String itemId, String endpointUri,
        String fileName) {
        UpdateSessionModel updateSessionModel = new UpdateSessionModel();
        updateSessionModel.setLibraryItemId(itemId);
        String sessionId = client.updateSession().create(null, updateSessionModel);
        logger.info("importFileFromHttpUriToItem, id: {}", sessionId);
        com.vmware.content.library.item.updatesession.FileTypes.AddSpec addSpec
            = new com.vmware.content.library.item.updatesession.FileTypes.AddSpec();
        addSpec.setName(fileName);
        addSpec.setSourceType(com.vmware.content.library.item.updatesession.FileTypes.SourceType.PULL);
        TransferEndpoint transferEndpoint = new TransferEndpoint();
        transferEndpoint.setUri(URI.create(endpointUri));
        addSpec.setSourceEndpoint(transferEndpoint);
        client.updateSessionFile().add(sessionId, addSpec);
        client.updateSession().complete(sessionId);
        return sessionId;
    }

    /**
     * Upload single file from local storage to item string.
     *
     * @param client the client
     * @param itemId the item id
     * @param fileName the file name
     * @param diskNum the disk num
     * @param path the path
     * @return the string
     */
    public static String uploadSingleFileFromLocalStorageToItem(ClsApiClient client, String itemId, String fileName,
        int diskNum, String path) {
        String filePath = path + "/" + fileName + ".ovf";
        UpdateSessionModel updateSessionModel = new UpdateSessionModel();
        updateSessionModel.setLibraryItemId(itemId);
        String sessionId = client.updateSession().create(getRandomClientToken(), updateSessionModel);
        logger.info("uploadSingleFileFromLocalStorageToItem, id: {}", sessionId);

        com.vmware.content.library.item.updatesession.FileTypes.AddSpec file
            = new com.vmware.content.library.item.updatesession.FileTypes.AddSpec();
        file.setName(fileName);
        file.setSourceType(com.vmware.content.library.item.updatesession.FileTypes.SourceType.PUSH);
        com.vmware.content.library.item.updatesession.FileTypes.Info fileInfo = client
            .updateSessionFile()
            .add(sessionId, file);
        URI uploadUri = fileInfo.getUploadEndpoint().getUri();
        TaskNodeThreadPoolManager.getInstance().execute(() -> {
            try {
                logger.info("start importing ovf, id: {}", sessionId);
                CommonUtil.uploadFile(new File(filePath), uploadUri);
                uploadTransferVmdkAndOvfToItem(file, client, sessionId, fileName, diskNum, path);
                client.updateSession().complete(sessionId);
                logger.info("end importing ovf, id : {}", sessionId);
            } catch (Exception e) {
                logger.error("upload file failed: ", e);
            }
        });
        return sessionId;
    }

    /**
     * Upload transfer vmdk and ovf to item.
     *
     * @param file the file
     * @param client the client
     * @param sessionId the session id
     * @param ovfAndDiskName the ovf and disk name
     * @param diskSum the disk sum
     * @param path the path
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyStoreException the key store exception
     * @throws KeyManagementException the key management exception
     * @throws IOException the io exception
     */
    public static void uploadTransferVmdkAndOvfToItem(FileTypes.AddSpec file, ClsApiClient client, String sessionId,
        String ovfAndDiskName, int diskSum, String path)
        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        file.setName(ovfAndDiskName + "1" + ".ovf");
        file.setSourceType(com.vmware.content.library.item.updatesession.FileTypes.SourceType.PUSH);
        FileTypes.Info fileInfo = client.updateSessionFile().add(sessionId, file);
        URI uploadUri = fileInfo.getUploadEndpoint().getUri();
        CommonUtil.uploadFile(new File(path + "/" + ovfAndDiskName + ".ovf"), uploadUri);
        for (int i = 1; i <= diskSum; i++) {
            file.setName(ovfAndDiskName + "-disk" + i + ".vmdk");
            file.setSourceType(com.vmware.content.library.item.updatesession.FileTypes.SourceType.PUSH);
            fileInfo = client.updateSessionFile().add(sessionId, file);
            uploadUri = fileInfo.getUploadEndpoint().getUri();
            CommonUtil.uploadFile(new File(path + "/" + ovfAndDiskName + "-disk" + i + ".vmdk"), uploadUri);
        }
    }

    /**
     * Library items log.
     *
     * @param client the client
     */
    public static void libraryItemsLog(ClsApiClient client) {
        Library library;
        try {
            library = client.library();
        } catch (Exception e) {
            logger.warn("deploy ovf runSample", e);
            client.login();
            logger.info("Logged in to Content Library API successfully.");
            library = client.library();
        }
        logger.info("List of items in all content libraries:");
        List<String> libraryIds = library.list();
        for (String libraryId : libraryIds) {
            LibraryModel libraryModel = library.get(libraryId);
            logger.info("{} (Id: {})", libraryModel.getName(), libraryId);
            itemsFromLibraryLog(client, libraryId);
        }
    }

    /**
     * Items from library log.
     *
     * @param client the client
     * @param libraryId the library id
     */
    public static void itemsFromLibraryLog(ClsApiClient client, String libraryId) {
        Item item = client.item();
        List<String> itemIds = item.list(libraryId);
        itemIds.forEach(id -> {
            ItemModel itemModel = item.get(id);
            logger.info("{} - Id: {} (type={})", itemModel.getName(), itemModel.getId(), itemModel.getType());
        });
    }
}
