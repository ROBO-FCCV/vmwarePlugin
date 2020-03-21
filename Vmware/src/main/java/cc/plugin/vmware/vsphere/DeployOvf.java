/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.vsphere;

import cc.plugin.vmware.model.vo.response.OvfVo;

import com.alibaba.fastjson.JSONObject;
import com.vmware.vcenter.ovf.LibraryItemTypes.DeploymentResult;
import com.vmware.vcenter.ovf.LibraryItemTypes.DeploymentTarget;
import com.vmware.vcenter.ovf.LibraryItemTypes.ResourcePoolDeploymentSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Deploy ovf.
 *
 * @since 2019 -10-18
 */
public class DeployOvf extends AbstractSample {
    @Override
    public OvfVo runSample(String[] args) {
        ClsApiHelper.libraryItemsLog(client);
        JSONObject imageJsonParamsObj = (JSONObject) JSONObject.parse(args[1]);
        String itemId = imageJsonParamsObj.getString("itemId");
        DeploymentTarget target = new DeploymentTarget();
        target.setResourcePoolId(imageJsonParamsObj.getString("resourcePoolId"));
        Map<String, String> networkMap = new HashMap<String, String>();
        String entityName = imageJsonParamsObj.getString("entityName");
        String datastoreId = imageJsonParamsObj.getString("datastoreId");
        ResourcePoolDeploymentSpec spec = createResourcePoolDeploymentSpec(entityName, true, networkMap, datastoreId);
        DeploymentResult result = client.ovfLibraryItem().deploy(null, itemId, target, spec);
        String messageOnSuccess = "OVF item deployment succeeded." + "\nDeployment information:" + " "
            + result.getResourceId().getType() + " " + result.getResourceId().getId();
        String messageOnFailure = "OVF item deployment failed.";
        OvfUtil.displayOperationResult(result.getSucceeded(), result.getError(), messageOnSuccess, messageOnFailure);
        client.login();
        client.item().delete(itemId);
        return new OvfVo().setResourceId(result.getResourceId().getId());
    }

    private ResourcePoolDeploymentSpec createResourcePoolDeploymentSpec(String entityName, boolean acceptAllEULA,
        Map<String, String> networkMap, String datastoreId) {
        ResourcePoolDeploymentSpec deploymentSpec = new ResourcePoolDeploymentSpec();
        deploymentSpec.setAcceptAllEULA(acceptAllEULA);
        deploymentSpec.setName(entityName);
        deploymentSpec.setAnnotation("This is a VM created using LibraryItem interface");
        if (networkMap != null) {
            deploymentSpec.setNetworkMappings(networkMap);
        }
        if (!datastoreId.isEmpty()) {
            deploymentSpec.setDefaultDatastoreId(datastoreId);
        }
        return deploymentSpec;
    }
}
