/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.ApplicationException;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.NetworkVo;
import cc.plugin.vmware.service.NetworkService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vmware.vim25.DVPortgroupConfigInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.Tag;
import com.vmware.vim25.VimPortType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @since 2019 -09-23
 */
@Service
public class NetworkServiceImpl implements NetworkService {
    private static final Logger logger = LoggerFactory.getLogger(NetworkServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    @Override
    @SuppressWarnings("unchecked")
    public List<NetworkVo> getNetworks(String vmwareId, String datacenterName, String hostName)
        throws CustomException {
        List<NetworkVo> result = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        ServiceUtil svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference datacenterRef = getManagedObj(datacenterName, content, service);
        if (datacenterRef == null) {
            throw new ApplicationException("The specified datacenter is not found");
        }
        List<ManagedObjectReference> networks;
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        networks = (List<ManagedObjectReference>) svc.getDynamicProperty(datacenterRef, "network");
        if (networks == null) {
            return result;
        }
        if (hostName == null) {
            return result;
        }
        if (hostName.contains("domain")) {
            ManagedObjectReference clusterSystem = new ManagedObjectReference();
            clusterSystem.setType("ClusterComputeResource");
            clusterSystem.setValue(hostName);
            List<ManagedObjectReference> clusterNetworks = (List<ManagedObjectReference>) svc.getDynamicProperty(
                clusterSystem, "network");
            setClusterNetWork(result, svc, clusterNetworks);
        } else {
            setNetworks(result, hostName, svc, networks);
        }
        return result;
    }

    @Override
    public List<String> getEsxiNetworks(String vmwareId, String hostName) throws CustomException {
        List<String> wwnList = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        if (svc != null && svc.connection != null) {
            ManagedObjectReference hostRef = svc.getDecendentMoRef(null, "HostSystem", hostName);
            List<ManagedObjectReference> netWorklist = (List<ManagedObjectReference>) svc.getDynamicProperty(hostRef,
                "network");
            for (ManagedObjectReference network : netWorklist) {
                // 过滤掉上行链路端口组
                String netWorkName = (String) svc.getDynamicProperty(network, "name");
                JSONObject netName = new JSONObject();
                netName.put("network", netWorkName);
                wwnList.add(netName.toString());
            }
        }
        return wwnList;
    }

    private ManagedObjectReference getManagedObj(String datacenterName, ServiceContent content, VimPortType service) {
        ManagedObjectReference datacenterRef = null;
        try {
            if (service != null && content != null) {
                datacenterRef = service.findByInventoryPath(content.getSearchIndex(), datacenterName);
            }
        } catch (RuntimeFaultFaultMsg e) {
            throw new ApplicationException("vmware findByInventoryPath fail ", e);
        }
        return datacenterRef;
    }

    private void setClusterNetWork(List<NetworkVo> networks, ServiceUtil svc,
        List<ManagedObjectReference> networkLists) {
        for (ManagedObjectReference network : networkLists) {
            generateNetworkVo(networks, svc, network);
        }
    }

    private void generateNetworkVo(List<NetworkVo> networks, ServiceUtil svc, ManagedObjectReference network) {
        String networkName = (String) svc.getDynamicProperty(network, "name");
        Map<String, Object> urn = setUrn(svc, network);
        urn.put("portgroupKey", network.getValue());
        urn.put("portgroupId", networkName);
        String urnJson = JSON.toJSONString(urn);
        networks.add(new NetworkVo().setName(networkName).setVlanId(network.getValue()).setUrn(urnJson));
    }

    private Map<String, Object> setUrn(ServiceUtil svc, ManagedObjectReference network) {
        Map<String, Object> urn = new HashMap<String, Object>();
        if (network.getValue().contains("dvportgroup")) {
            urn.put("portgroupType", "dvs");
            DVPortgroupConfigInfo configInfo = (DVPortgroupConfigInfo) svc.getDynamicProperty(network, "config");
            if (configInfo != null) {
                String uuid = (String) svc.getDynamicProperty(configInfo.getDistributedVirtualSwitch(), "uuid");
                urn.put("dvsUUID", uuid);
            }
        } else {
            urn.put("portgroupType", "network");
        }
        return urn;
    }

    @SuppressWarnings("unchecked")
    private void setNetworks(List<NetworkVo> networks, String hostName, ServiceUtil svc, List<ManagedObjectReference> networkArray) {
        for (ManagedObjectReference network : networkArray) {
            // 过滤掉上行链路端口组
            List<Tag> tag = (List<Tag>) svc.getDynamicProperty(network, "tag");
            if (tag != null && tag.size() > 0 && tag.get(0).getKey().contains("DVS.UPLINK")) {
                continue;
            }
            List<ManagedObjectReference> hostArray = (List<ManagedObjectReference>) svc.getDynamicProperty(network,
                "host");
            if (hostArray != null) {
                iteratorHosts(hostArray, svc, hostName, network, networks);
            }
        }
    }

    private void iteratorHosts(List<ManagedObjectReference> hostArray, ServiceUtil svc, String hostName,
        ManagedObjectReference network, List<NetworkVo> networks) {
        for (ManagedObjectReference host : hostArray) {
            setNetWork(svc, hostName, network, networks, host);
        }
    }

    private void setNetWork(ServiceUtil svc, String hostName, ManagedObjectReference network, List<NetworkVo> networks,
        ManagedObjectReference host) {
        String ip = (String) svc.getDynamicProperty(host, "name");
        if (null != hostName && hostName.equals(ip)) {
            generateNetworkVo(networks, svc, network);
        }
    }
}
