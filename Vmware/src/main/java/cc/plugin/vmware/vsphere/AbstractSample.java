/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.vsphere;

import cc.plugin.vmware.model.vo.response.OvfVo;
import cc.plugin.vmware.token.LibraryCache;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * The type Abstract sample.
 *
 * @since 2019 -09-19
 */
public abstract class AbstractSample {

    /**
     * The Client.
     */
    protected ClsApiClient client;

    /**
     * Run sample ovf vo.
     *
     * @param args the args
     * @return the ovf vo
     * @throws IOException the io exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyStoreException the key store exception
     * @throws KeyManagementException the key management exception
     */
    protected abstract OvfVo runSample(String[] args)
        throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException;

    /**
     * Run ovf vo.
     *
     * @param args the args
     * @return the ovf vo
     */
    public OvfVo run(String[] args) {
        OvfVo runback;
        setUp(args);
        try {
            runback = runSample(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            tearDown();
        }
        return runback;
    }


    private void setUp(String[] args) {
        JSONObject verifyJson = (JSONObject) JSONObject.parse(args[0]);
        if (verifyJson == null) {
            return;
        }
        String ip = verifyJson.getString("ip");
        String username = verifyJson.getString("serviceAuthUser");
        String password = verifyJson.getString("serviceAuthKey");
        String vmwareId = verifyJson.getString("vmwareId");
        client = LibraryCache.setClient(vmwareId, ip, username, password);
    }


    private void tearDown() {
        VimServiceManager.disConnect();
        client.logout();
    }
}
