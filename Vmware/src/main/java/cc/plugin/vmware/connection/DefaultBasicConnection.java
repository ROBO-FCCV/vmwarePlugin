/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.connection;

import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.ApplicationException;
import cc.plugin.vmware.exception.CustomException;

import com.google.common.collect.Maps;
import com.vmware.connection.Connection;
import com.vmware.connection.ConnectionMalformedUrlException;
import com.vmware.vim25.AboutInfo;
import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

/**
 * The type Default basic connection.
 *
 * @since 2019 -09-19
 */
public class DefaultBasicConnection implements Connection {
    private static final int MIN_VERSION = 6;

    private static final int MAX_VERSION = 7;

    private static VimService vimService;

    static {
        vimService = new VimService();
    }

    private ManagedObjectReference svcInstance;
    private UserSession userSession;
    private VimPortType vimPort;
    private ServiceContent serviceContent;
    private String username;
    private String helso = "";
    private URL url;
    private Map headers;

    /**
     * Instantiates a new Default basic connection.
     */
    public DefaultBasicConnection() {
        vimPort = vimService.getVimPort();
    }

    public String getUrl() {
        return url.toString();
    }

    public void setUrl(String url) {
        try {
            URL newUrl = new URL(url);
            this.url = newUrl;
        } catch (MalformedURLException e) {
            throw new ConnectionMalformedUrlException("malformed URL argument:", e);
        }
    }

    /**
     * getPort
     *
     * @return Integer
     */
    public Integer getPort() {
        return url.getPort();
    }

    /**
     * getUsername
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * setUsername
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getPassword
     *
     * @return String
     */
    public String getPassword() {
        return this.helso;
    }

    /**
     * setPassword
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.helso = password;
    }

    /**
     * getServiceInstanceName
     *
     * @return String
     */
    public String getServiceInstanceName() {
        return "ServiceInstance";
    }

    /**
     * getHost
     *
     * @return String
     */
    public String getHost() {
        return url.getHost();
    }

    /**
     * getVimService
     *
     * @return VimService
     */
    public VimService getVimService() {
        return vimService;
    }

    /**
     * getServiceContent
     *
     * @return ServiceContent
     */
    public ServiceContent getServiceContent() {
        return serviceContent;
    }

    /**
     * getUserSession
     *
     * @return UserSession
     */
    public UserSession getUserSession() {
        return userSession;
    }

    public VimPortType getVimPort() {
        return vimPort;
    }

    public Map getHeaders() {
        return headers;
    }

    /**
     * getServiceInstanceReference
     *
     * @return ManagedObjectReference
     */
    public ManagedObjectReference getServiceInstanceReference() {
        if (svcInstance == null) {
            ManagedObjectReference mof = new ManagedObjectReference();
            mof.setType(this.getServiceInstanceName());
            mof.setValue(this.getServiceInstanceName());
            svcInstance = mof;
        }
        return svcInstance;
    }

    /**
     * Check vmware info connection.
     *
     * @return the connection
     * @throws CustomException 自定义异常
     */
    public Connection checkVmwareInfo() throws CustomException {
        vmwareInfoValidation();
        return this;
    }

    /**
     * Connect
     *
     * @return Connection
     */
    public Connection connect() {
        connectInParent();
        return this;
    }

    private void connectInParent() {
        try {
            Map<String, Object> ctxt = ((BindingProvider) vimPort).getRequestContext();
            setCtxt(ctxt);
            serviceContent = vimPort.retrieveServiceContent(this.getServiceInstanceReference());
            headers = (Map) ((BindingProvider) vimPort).getResponseContext().get(MessageContext.HTTP_RESPONSE_HEADERS);
            userSession = vimPort.login(serviceContent.getSessionManager(), username, helso, null);
        } catch (InvalidLocaleFaultMsg | InvalidLoginFaultMsg | RuntimeFaultFaultMsg e) {
            throw new ApplicationException("vmware login fail ", e);
        }
    }

    private void vmwareInfoValidation() throws CustomException {
        Map<String, Object> ctxt = Maps.newHashMap();
        try {
            if (vimPort instanceof BindingProvider) {
                BindingProvider bindingProvider = (BindingProvider) vimPort;
                ctxt = bindingProvider.getRequestContext();
            }
            setCtxt(ctxt);
            serviceContent = vimPort.retrieveServiceContent(this.getServiceInstanceReference());
            headers = (Map) ((BindingProvider) vimPort).getResponseContext().get(MessageContext.HTTP_RESPONSE_HEADERS);
            userSession = vimPort.login(serviceContent.getSessionManager(), username, helso, null);
            AboutInfo aboutInfo = serviceContent.getAbout();
            String version = aboutInfo.getVersion();
            String licenseProductName = aboutInfo.getLicenseProductName();
            versionCheck(version);
            productCheck(licenseProductName);
            vimPort.logout(serviceContent.getSessionManager());
        } catch (InvalidLocaleFaultMsg | InvalidLoginFaultMsg | RuntimeFaultFaultMsg e) {
            throw new ApplicationException("Vmware login fail ", e);
        } catch (CustomException ex) {
            throw new CustomException(ex.getErrorCode(), ex.getMessage());
        }
    }

    private void productCheck(String licenseProductName) throws CustomException {
        if (StringUtils.isBlank(licenseProductName)) {
            return;
        }
        if (StringUtils.containsIgnoreCase(licenseProductName, "ESX")) {
            throw new CustomException(ErrorCode.CONNECT_VCENTER_PRODUCT_ERROR_CODE,
                ErrorCode.CONNECT_VCENTER_PRODUCT_ERROR_MSG);
        }
    }

    private void versionCheck(String version) throws CustomException {
        if (StringUtils.isBlank(version)) {
            return;
        }
        String versionNew = StringUtils.split(version, "\\.")[0];
        if (Integer.parseInt(versionNew) < MIN_VERSION || Integer.parseInt(versionNew) > MAX_VERSION) {
            throw new CustomException(ErrorCode.CONNECT_VCENTER_VERSION_ERROR_CODE,
                ErrorCode.CONNECT_VCENTER_VERSION_ERROR_MSG);
        }
    }

    private void setCtxt(Map<String, Object> ctxt) {
        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    /**
     * isConnected
     *
     * @return boolean
     */
    public boolean isConnected() {
        if (userSession == null) {
            return false;
        }
        long startTime = userSession.getLastActiveTime().toGregorianCalendar().getTime().getTime();
        return new Date().getTime() < startTime + 30 * 60 * 1000;
    }

    /**
     * disconnect
     *
     * @return Connection
     */
    public Connection disconnect() {
        if (userSession != null) {
            try {
                vimPort.logout(serviceContent.getSessionManager());
            } catch (RuntimeFaultFaultMsg e) {
                throw new ApplicationException("vmware logout fail ", e);
            } finally {
                vimPort = null;
                userSession = null;
                vimService = null;
                serviceContent = null;
            }
        }
        return this;
    }
}
