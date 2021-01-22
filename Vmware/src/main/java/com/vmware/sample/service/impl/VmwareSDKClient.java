/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.vmware.authorization.utils.TrustAllTrustManager;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TraversalSpec;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

/**
 * Abstract SDK service
 *
 * @since 2020-09-14
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VmwareSDKClient {
    private final ConcurrentHashMap<String, VMwareSDK> stubConfigurationMap;

    /**
     * Get sdk
     *
     * @param vmwareId VMware id
     * @return sdk instance
     */
    protected VMwareSDK getSDKInstance(String vmwareId) {
        if (!stubConfigurationMap.containsKey(vmwareId)) {
            throw new PluginException(RestCodeEnum.VMWARE_NOT_EXISTED);
        }
        VMwareSDK vMwareSDK = stubConfigurationMap.get(vmwareId);
        trustAllCert();
        boolean result = vMwareSDK.checkSession();
        if (!result) {
            log.error("Session was empire.Maybe password was changed by remote.");
            throw new PluginException(RestCodeEnum.CONNECTION_EXCEPTION);
        }
        return vMwareSDK;
    }

    private void trustAllCert() {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new TrustAllTrustManager();
        trustAllCerts[0] = tm;
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            SSLSessionContext sslSessionContext = sc.getServerSessionContext();
            sslSessionContext.setSessionTimeout(Math.toIntExact(TimeUnit.HOURS.toSeconds(1L)));
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier hv = (urlHostName, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("NoSuchAlgorithmException | KeyManagementException",
                SensitiveExceptionUtils.hideSensitiveInfo(e));
            throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * Retrieve Properties
     *
     * @param sdkInstance sdk instance
     * @param managedObjectReference managedObject for retrieve
     * @param properties properties
     * @return retrieve result
     */
    protected List<ObjectContent> retrieveProperties(VMwareSDK sdkInstance,
        ManagedObjectReference managedObjectReference, List<String> properties) {
        try {
            return sdkInstance.getVimPort()
                .retrieveProperties(sdkInstance.getServiceContent().getPropertyCollector(),
                    generatePropertyFilterSpec(managedObjectReference, properties));
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg invalidPropertyFaultMsg) {
            log.error("InvalidPropertyFaultMsg | RuntimeFaultFaultMsg",
                SensitiveExceptionUtils.hideSensitiveInfo(invalidPropertyFaultMsg));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    /**
     * Get propertyFilterSpecs
     *
     * @param objectReference current object
     * @param props retrieve props
     * @return PropertyFilterSpecs
     */
    protected List<PropertyFilterSpec> generatePropertyFilterSpec(ManagedObjectReference objectReference,
        List<String> props) {
        PropertySpec propSpec = new PropertySpec();
        propSpec.setAll(false);
        propSpec.getPathSet().addAll(props);
        propSpec.setType(objectReference.getType());

        ObjectSpec objSpec = new ObjectSpec();
        objSpec.setObj(objectReference);
        objSpec.setSkip(false);

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.getPropSet().add(propSpec);
        spec.getObjectSet().add(objSpec);
        return Collections.singletonList(spec);
    }

    /**
     * retrieve Objects With Props from retrievedObject
     *
     * @param vMwareSDK VMware sdk instance
     * @param retrievedObject the retrieved object
     * @param traversalSpecs the retrieve objects traversalSpecs
     * @param retrieveObjects the retrieve objects
     * @param retrieveProperties the retrieve properties
     * @return retrieve result
     */
    protected List<ObjectContent> retrieveObjectsWithPropsBelowManagedObjRef(VMwareSDK vMwareSDK,
        ManagedObjectReference retrievedObject, List<TraversalSpec> traversalSpecs, List<String> retrieveObjects,
        List<String> retrieveProperties) {
        List<PropertySpec> propertySpecs = new ArrayList<>();
        for (String retrieveObject : retrieveObjects) {
            PropertySpec propertySpec = new PropertySpec();
            propertySpec.setType(retrieveObject);
            propertySpec.setAll(false);
            propertySpec.getPathSet().addAll(retrieveProperties);
            propertySpecs.add(propertySpec);
        }
        List<ObjectSpec> objSpecList = new ArrayList<>();
        ObjectSpec objSpec = new ObjectSpec();
        objSpec.setObj(retrievedObject);
        objSpec.setSkip(true);
        objSpec.getSelectSet().addAll(traversalSpecs);
        objSpecList.add(objSpec);
        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getPropSet().addAll(propertySpecs);
        propertyFilterSpec.getObjectSet().addAll(objSpecList);
        try {
            return vMwareSDK.getVimPort()
                .retrieveProperties(vMwareSDK.getServiceContent().getPropertyCollector(),
                    Collections.singletonList(propertyFilterSpec));
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg invalidPropertyFaultMsg) {
            log.error("RuntimeFaultFaultMsg|InvalidPropertyFaultMsg",
                SensitiveExceptionUtils.hideSensitiveInfo(invalidPropertyFaultMsg));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    /**
     * Retrieve objects with properties from root
     *
     * @param vMwareSDK vmware sdk
     * @param retrieveObjects retrieve objects
     * @param retrieveProperties retrieve properties
     * @return retrieve result
     */
    protected List<ObjectContent> retrieveObjectWithProperties(VMwareSDK vMwareSDK, List<String> retrieveObjects,
        List<String> retrieveProperties) {
        ManagedObjectReference viewMgrRef = vMwareSDK.getServiceContent().getViewManager();
        ManagedObjectReference propColl = vMwareSDK.getServiceContent().getPropertyCollector();

        try {
            ManagedObjectReference cViewRef = vMwareSDK.getVimPort()
                .createContainerView(viewMgrRef, vMwareSDK.getServiceContent().getRootFolder(), retrieveObjects, true);
            ObjectSpec oSpec = new ObjectSpec();
            oSpec.setObj(cViewRef);
            oSpec.setSkip(true);

            TraversalSpec tSpec = new TraversalSpec();
            tSpec.setName("traverseEntities");
            tSpec.setPath("view");
            tSpec.setSkip(false);
            tSpec.setType("ContainerView");

            oSpec.getSelectSet().add(tSpec);
            List<PropertySpec> propertySpecs = new ArrayList<>();
            for (String retrieveObject : retrieveObjects) {
                PropertySpec pSpec = new PropertySpec();
                pSpec.setType(retrieveObject);
                pSpec.getPathSet().addAll(retrieveProperties);
                propertySpecs.add(pSpec);
            }

            PropertyFilterSpec fSpec = new PropertyFilterSpec();
            fSpec.getObjectSet().add(oSpec);
            fSpec.getPropSet().addAll(propertySpecs);

            List<PropertyFilterSpec> fSpecList = new ArrayList<>();
            fSpecList.add(fSpec);

            return vMwareSDK.getVimPort().retrieveProperties(propColl, fSpecList);
        } catch (RuntimeFaultFaultMsg | InvalidPropertyFaultMsg e) {
            log.error("RuntimeFaultFaultMsg|InvalidPropertyFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }
}
