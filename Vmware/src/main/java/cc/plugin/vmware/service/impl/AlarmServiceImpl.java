/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.VcenterAlarmInfo;
import cc.plugin.vmware.service.AlarmService;

import com.vmware.vim25.AlarmInfo;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 获取vCenter告警
 *
 * @since 2019 -09-23
 */
@Service
class AlarmServiceImpl implements AlarmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    private final static List<String> ALARMOBJECTS = Arrays.asList("HostSystem", "Datastore", "VirtualMachine",
        "ClusterComputeResource", "Datacenter", "VmwareDistributedVirtualSwitch", "DistributedVirtualPortgroup",
        "HostDatastoreSystem");

    private static String getObjectName(VimPortType service, ManagedObjectReference propertyMgr,
        ManagedObjectReference mor) throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        String objectName = null;
        PropertySpec propSpec = new PropertySpec();
        propSpec.setAll(Boolean.FALSE);
        propSpec.getPathSet().add("name");
        propSpec.setType(mor.getType());
        ObjectSpec objSpec = new ObjectSpec();
        objSpec.setObj(mor);
        objSpec.setSkip(Boolean.FALSE);
        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.getPropSet().add(propSpec);
        spec.getObjectSet().add(objSpec);
        ArrayList<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>();
        listpfs.add(spec);
        List<ObjectContent> listobjcont = service.retrieveProperties(propertyMgr, listpfs);
        if (listobjcont != null) {
            ObjectContent oc = listobjcont.get(0);
            objectName = (String) oc.getPropSet().get(0).getVal();
        }
        return objectName;
    }

    /**
     * Gets alarms.
     *
     * @param alarmManager the alarm manager
     * @param object the object
     * @param vimPort the vim port
     * @param serviceContent the service content
     * @return the alarms
     */
    public List<VcenterAlarmInfo> getAlarms(ManagedObjectReference alarmManager, ManagedObjectReference object,
        VimPortType vimPort, ServiceContent serviceContent) {
        List<VcenterAlarmInfo> alarmLists = new ArrayList<>();
        List<AlarmState> list;
        try {
            list = vimPort.getAlarmState(alarmManager, object);
            if (list == null) {
                return alarmLists;
            }
            for (AlarmState alarmState : list) {
                if (alarmState.getOverallStatus().toString().equals("YELLOW") || alarmState
                    .getOverallStatus()
                    .toString()
                    .equals("RED")) {
                    alarmLists.add(alarmDataHandle(serviceContent, vimPort, alarmState));
                }
            }
        } catch (RuntimeFaultFaultMsg e) {
            LOGGER.error("getAlarms RuntimeFaultFaultMsg is " + e);
        } catch (InvalidPropertyFaultMsg e) {
            LOGGER.error("getAlarms InvalidPropertyFaultMsg is " + e);
        }
        return alarmLists;
    }

    @Override
    public List<VcenterAlarmInfo> getAlarmCurrent(String vmwareId) throws CustomException {
        List<VcenterAlarmInfo> totalAlarms = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection s1;
        ServiceUtil svc;
        ecb.connect();
        s1 = ecb.getConnection();
        if (s1 == null) {
            return totalAlarms;
        }
        svc = ecb.getServiceUtil();
        if (svc.connection == null) {
            return totalAlarms;
        }
        ServiceContent serviceContent = s1.getServiceContent();
        ManagedObjectReference alarmManager = serviceContent.getAlarmManager();
        VimPortType vimPort = s1.getVimPort();
        // 首先获取设备层面告警
        ManagedObjectReference rootFolder = s1.getRootFolder();
        List<VcenterAlarmInfo> rootAlarmInfos = getAlarms(alarmManager, rootFolder, vimPort, serviceContent);
        totalAlarms.addAll(
            CollectionUtils.isEmpty(rootAlarmInfos) ? new ArrayList<VcenterAlarmInfo>() : rootAlarmInfos);
        // 分层次统计
        for (String alarmObject : ALARMOBJECTS) {
            List<ManagedObjectReference> objectRels;
            try {
                objectRels = svc.getDecendentMoRefs(rootFolder, alarmObject);
                if (null == objectRels) {
                    continue;
                }
                for (ManagedObjectReference objectRel : objectRels) {
                    List<VcenterAlarmInfo> partAlarms = getAlarms(alarmManager, objectRel, vimPort, serviceContent);
                    totalAlarms.addAll(
                        CollectionUtils.isEmpty(partAlarms) ? new ArrayList<VcenterAlarmInfo>() : partAlarms);
                }
            } catch (Exception e) {
                LOGGER.error("getDecendentMoRefs InvalidPropertyFaultMsg is {}", e);
                return totalAlarms;
            }
        }
        return totalAlarms;
    }

    private static PropertyFilterSpec createAlarmFilterSpec(ManagedObjectReference eventHistoryCollectorRef) {
        PropertySpec propSpec = new PropertySpec();
        propSpec.setAll(Boolean.FALSE);
        propSpec.getPathSet().add("info");
        propSpec.setType(eventHistoryCollectorRef.getType());
        ObjectSpec objSpec = new ObjectSpec();
        objSpec.setObj(eventHistoryCollectorRef);
        objSpec.setSkip(Boolean.FALSE);
        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.getPropSet().add(propSpec);
        spec.getObjectSet().add(objSpec);
        return spec;
    }

    private VcenterAlarmInfo alarmDataHandle(ServiceContent content, VimPortType service, AlarmState alarmState)
        throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        VcenterAlarmInfo alarmItem = new VcenterAlarmInfo();
        ManagedObjectReference alarm = alarmState.getAlarm();
        if (alarm != null) {
            PropertyFilterSpec alarmFilterSpec = createAlarmFilterSpec(alarm);
            List<ObjectContent> listobjcont = service.retrieveProperties(content.getPropertyCollector(),
                Arrays.asList(alarmFilterSpec));
            if (listobjcont != null) {
                ObjectContent oc = listobjcont.get(0);
                AlarmInfo alarmInfo = (AlarmInfo) oc.getPropSet().get(0).getVal();
                alarmItem.setAlarmName(alarmInfo.getName());
                alarmItem.setDescription(alarmInfo.getDescription());
            }
        }
        alarmItem.setAlarmId(alarmState.getKey());
        alarmItem.setAcknowledged(alarmState.isAcknowledged());
        alarmItem.setAcknowledgedByUser(alarmState.getAcknowledgedByUser());
        if (alarmState.getAcknowledgedTime() != null) {
            Date ackedDate = alarmState.getAcknowledgedTime().toGregorianCalendar().getTime();
            alarmItem.setAcknowledgedTime(ackedDate);
        }
        alarmItem.setOverallStatus(alarmState.getOverallStatus());
        alarmItem.setTime(alarmState.getTime().toGregorianCalendar().getTime());
        alarmItem.setObjectName(getObjectName(service, content.getPropertyCollector(), alarmState.getEntity()));
        return alarmItem;
    }
}
