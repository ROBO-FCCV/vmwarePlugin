/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.model.library.file.UpSessionFileCreate;
import com.vmware.sample.model.vmtemplate.DeploymentSpec;
import com.vmware.sample.model.vmtemplate.ResourcePoolDeploySpec;
import com.vmware.sample.service.LibraryItemUpSessionFileService;
import com.vmware.sample.service.VmTemplateService;

import com.vmware.content.library.item.FileTypes.ChecksumAlgorithm;
import com.vmware.content.library.item.FileTypes.ChecksumInfo;
import com.vmware.content.library.item.TransferEndpoint;
import com.vmware.content.library.item.updatesession.FileTypes;
import com.vmware.vcenter.ovf.DiskProvisioningType;
import com.vmware.vcenter.ovf.LibraryItemTypes;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract vmware controller
 *
 * @since 2020-10-16
 */
public class AbstractVMwareController {
    /**
     * Copy storage group mapping properties
     *
     * @param resourcePoolDeploymentSpec resource pool deployment spec
     * @param resourcePoolDeploySpec source properties
     */
    protected void copyStorageGroupMapping(LibraryItemTypes.ResourcePoolDeploymentSpec resourcePoolDeploymentSpec,
        ResourcePoolDeploySpec resourcePoolDeploySpec) {
        Map<String, ResourcePoolDeploySpec.StorageGroupMapping> storageMappings
            = resourcePoolDeploySpec.getStorageMappings();
        Map<String, LibraryItemTypes.StorageGroupMapping> stringStorageGroupMappingMap = new HashMap<>();
        if (MapUtils.isNotEmpty(storageMappings)) {
            for (String key : storageMappings.keySet()) {
                LibraryItemTypes.StorageGroupMapping storageGroupMapping = new LibraryItemTypes.StorageGroupMapping();
                storageGroupMapping.setDatastoreId(storageMappings.get(key).getDatastoreId());
                storageGroupMapping.setProvisioning(
                    DiskProvisioningType.valueOf(storageMappings.get(key).getProvisioning()));
                storageGroupMapping.setStorageProfileId(storageMappings.get(key).getStorageProfileId());
                storageGroupMapping.setType(
                    LibraryItemTypes.StorageGroupMapping.Type.valueOf(storageMappings.get(key).getType()));
                stringStorageGroupMappingMap.put(key, storageGroupMapping);
            }
            resourcePoolDeploymentSpec.setStorageMappings(stringStorageGroupMappingMap);
        }
    }

    /**
     * Deploy ovf
     *
     * @param vmTemplateService service
     * @param vmwareId vmware id
     * @param libraryItemId library item id
     * @param deploymentSpec deploy spec
     * @return result
     */
    protected LibraryItemTypes.DeploymentResult deployOvf(VmTemplateService vmTemplateService, String vmwareId,
        String libraryItemId, DeploymentSpec deploymentSpec) {
        LibraryItemTypes.DeploymentTarget deploymentTarget = new LibraryItemTypes.DeploymentTarget();
        deploymentTarget.setResourcePoolId(deploymentSpec.getResourcePoolId());
        LibraryItemTypes.ResourcePoolDeploymentSpec resourcePoolDeploymentSpec
            = new LibraryItemTypes.ResourcePoolDeploymentSpec();
        ResourcePoolDeploySpec resourcePoolDeploySpec = deploymentSpec.getResourcePoolDeploySpec();
        BeanUtils.copyProperties(resourcePoolDeploySpec, resourcePoolDeploymentSpec);
        copyStorageGroupMapping(resourcePoolDeploymentSpec, resourcePoolDeploySpec);
        if (resourcePoolDeploySpec.getStorageProvisioning() != null) {
            resourcePoolDeploymentSpec.setStorageProvisioning(
                DiskProvisioningType.valueOf(resourcePoolDeploySpec.getStorageProvisioning()));
        }
        return vmTemplateService.deploy(vmwareId, libraryItemId, deploymentTarget, resourcePoolDeploymentSpec);
    }

    /**
     * Add library item file
     *
     * @param libraryItemUpSessionFileService service
     * @param vmwareId vmware id
     * @param sessionId session id
     * @param upSessionFileCreate up session file spec
     * @return result
     */
    protected FileTypes.Info addLibraryItemFile(LibraryItemUpSessionFileService libraryItemUpSessionFileService,
        String vmwareId, String sessionId, UpSessionFileCreate upSessionFileCreate) {
        FileTypes.AddSpec addSpec = new FileTypes.AddSpec();
        addSpec.setName(upSessionFileCreate.getName());
        addSpec.setSourceType(FileTypes.SourceType.valueOf(upSessionFileCreate.getSourceType()));
        if (ObjectUtils.isNotEmpty(upSessionFileCreate.getChecksumInfo())) {
            ChecksumInfo checksumInfo = new ChecksumInfo();
            checksumInfo.setAlgorithm(ChecksumAlgorithm.valueOf(upSessionFileCreate.getChecksumInfo().getAlgorithm()));
            checksumInfo.setChecksum(upSessionFileCreate.getChecksumInfo().getChecksum());
            addSpec.setChecksumInfo(checksumInfo);
        }
        if (ObjectUtils.isNotEmpty(upSessionFileCreate.getSourceEndpoint())) {
            TransferEndpoint transferEndpoint = new TransferEndpoint();
            transferEndpoint.setSslCertificateThumbprint(
                upSessionFileCreate.getSourceEndpoint().getSslCertificateThumbprint());
            transferEndpoint.setUri(upSessionFileCreate.getSourceEndpoint().getUri());
            addSpec.setSourceEndpoint(transferEndpoint);
        }
        return libraryItemUpSessionFileService.add(vmwareId, sessionId, addSpec);
    }
}
