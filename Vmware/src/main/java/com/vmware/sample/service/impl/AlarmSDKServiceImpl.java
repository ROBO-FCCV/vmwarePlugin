/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.model.alarm.Alarm;
import com.vmware.sample.service.AlarmService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vim25.AlarmInfo;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Alarm sdk service impl
 *
 * @since 2020-09-15
 */
@Slf4j
@Service("alarm-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AlarmSDKServiceImpl implements AlarmService {
    private final ObjectMapper objectMapper;

    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public List<Alarm> currentAlarms(String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> triggeredAlarmState = vmwareSDKClient.retrieveProperties(sdkInstance,
            sdkInstance.getServiceContent().getRootFolder(), Collections.singletonList("triggeredAlarmState"));
        List<DynamicProperty> propSet = triggeredAlarmState.get(0).getPropSet();
        Object val = propSet.get(0).getVal();
        List<Alarm> alarms = new ArrayList<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(val));
            Iterator<JsonNode> alarmStates = jsonNode.path("alarmState").elements();
            while (alarmStates.hasNext()) {
                JsonNode next = alarmStates.next();
                AlarmState alarmState = objectMapper.readValue(next.toString(), AlarmState.class);
                Alarm alarm = new Alarm();
                BeanUtils.copyProperties(alarmState, alarm);
                if (alarmState.getTime() != null) {
                    alarm.setTime(alarmState.getTime().toGregorianCalendar().getTime().getTime());
                }
                alarm.setAlarmId(alarmState.getAlarm().getValue());
                if (alarmState.getAcknowledgedTime() != null) {
                    alarm.setAcknowledgedTime(
                        alarmState.getAcknowledgedTime().toGregorianCalendar().getTime().getTime());
                }
                alarm.setAcknowledgedByUser(alarmState.getAcknowledgedByUser());
                alarm.setAcknowledged(alarmState.isAcknowledged());
                AlarmInfo alarmInfo = getAlarmInfo(sdkInstance, alarm);
                alarm.setAlarmName(alarmInfo.getName());
                alarm.setDescription(alarmInfo.getDescription());
                alarm.setObjectName(alarmState.getEntity().getValue());
                String objectName = getObjectInfo(sdkInstance, alarmState.getEntity());
                alarm.setObjectName(objectName);
                alarm.setObjectId(alarmState.getEntity().getValue());
                alarms.add(alarm);
            }
            return alarms;
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        return alarms;
    }

    private String getObjectInfo(VMwareSDK sdkInstance, ManagedObjectReference value) {
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, value,
            Collections.singletonList("name"));
        return objectContents.get(0).getPropSet().get(0).getVal().toString();
    }

    private AlarmInfo getAlarmInfo(VMwareSDK sdkInstance, Alarm alarm) throws JsonProcessingException {
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type("Alarm").value(alarm.getAlarmId()).build(),
            Collections.singletonList("info"));
        Object val1 = objectContents.get(0).getPropSet().get(0).getVal();
        return objectMapper.readValue(objectMapper.writeValueAsString(val1), AlarmInfo.class);
    }
}
