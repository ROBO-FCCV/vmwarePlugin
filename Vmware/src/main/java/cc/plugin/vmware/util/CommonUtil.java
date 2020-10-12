/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.VmwareInfo;
import cc.plugin.vmware.model.vo.response.ValidationResponse;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineConfigInfo;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * The type Common util.
 *
 * @since 2019 -10-15
 */
@Component
public class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private static final String IPV4 = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";

    private static Pattern IPV4_PATTERN = null;

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    static {
        try {
            IPV4_PATTERN = Pattern.compile(IPV4, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            logger.error("Ip pattern address exception: ", e);
        }
    }

    /**
     * Is ipv 4 address boolean.
     *
     * @param ipAddress the ip address
     * @return the boolean
     */
    public static boolean isIpv4Address(String ipAddress) {
        if (StringUtils.isEmpty(ipAddress)) {
            return false;
        }
        Matcher m1 = IPV4_PATTERN.matcher(ipAddress);
        return m1.matches();
    }

    /**
     * 保留一位小数(未四舍五入)
     *
     * @param d the d
     * @return the double
     */
    public static double roundToTheNearestTenth(double d) {
        try {
            String strD = String.valueOf(d * 10);
            String[] strArr = strD.split("\\.");
            return Double.parseDouble(strArr[0]) / 10;
        } catch (Exception e) {
            logger.error("OneAfterPoint error.", e);
            return 0;
        }
    }

    /**
     * Filter symbol string.
     *
     * @param str the str
     * @return the string
     */
    public static String filterSymbol(String str) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(str);
        StringBuilder result = new StringBuilder().append("vm-new");
        while (matcher.find()) {
            result.append(matcher.group());
        }
        return result.toString();
    }

    /**
     * Generate uuid string.
     *
     * @return the string
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Ip can be connected diff ip version boolean.
     *
     * @param ipAddress the ip address
     * @param ipVersion the ip version
     * @return the boolean
     * @throws CustomException 自定义异常
     */
    public static boolean ipCanBeConnectedDiffIpVersion(String ipAddress, String ipVersion) throws CustomException {
        // 校验IP
        Pattern pattern = Pattern.compile(Constant.IP_RULE);
        Matcher matcher = pattern.matcher(ipAddress);
        if (!matcher.matches()) {
            logger.info("IpCheckUtil.ipCanBeConnected ip is error. ip : {}", ipAddress);
            return false;
        }
        logger.info("Match ip complete.");
        // 校验ipVersion
        if (verSionNotValid(ipVersion)) {
            logger.error("ipVersion value {} invalid", ipVersion);
            return false;
        }
        logger.info("End verify ipVersion ... ");
        // 默认linux的ping命令
        String pingCommand = isIpv4Address(ipAddress) ? "ping" : "ping6";
        if (SystemUtils.IS_OS_WINDOWS) {
            pingCommand += " " + ipAddress + " -n " + Constant.SINGLE_PING_IP_TIMES + " -w "
                + Constant.SINGLE_PING_IP_LINUX_TIME_OUT;
        } else if (SystemUtils.IS_OS_LINUX) {
            pingCommand += " " + ipAddress + " -c " + Constant.SINGLE_PING_IP_TIMES + " -W "
                + Constant.SINGLE_PING_IP_LINUX_TIME_OUT;
        } else {
            logger.info("This os wasn't support.");
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        logger.info("PingCommand : {}", pingCommand);
        return getResult(pingCommand);
    }

    private static boolean getResult(String pingCommand) {
        boolean result = false;
        Process process = null;
        InputStream inputStreams = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            // 执行命令并获取输出
            process = runtime.exec(pingCommand);
            inputStreams = process.getInputStream();
            PingProcessStreamHandler inputHandlerObj = new PingProcessStreamHandler(inputStreams);
            Future<Boolean> future = TaskNodeThreadPoolManager.getInstance().submitBooleanThread(inputHandlerObj);
            // 错误流处理
            InputStream errorStream = process.getErrorStream();
            PingErrorStreamHandler errorHandler = new PingErrorStreamHandler(errorStream);
            TaskNodeThreadPoolManager.getInstance().execute(errorHandler);
            result = getPingResult(future);
        } catch (IOException e) {
            logger.info("IOException is Unreachable.", e);
        } finally {
            try {
                if (inputStreams != null) {
                    inputStreams.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                logger.error("IO close exception", e);
            }
        }
        return result;
    }

    private static Boolean getPingResult(Future<Boolean> future) {
        Boolean result = false;
        try {
            result = future.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("GetPingResult failure...", e);
        } catch (ExecutionException e) {
            logger.error("GetPingResult failure...", e);
        } catch (TimeoutException e) {
            logger.error("Ping time out.", e);
        }
        return result;
    }

    /**
     * Validate vmware info validation response.
     *
     * @param vmwareInfo the vmware info
     * @return the validation response
     * @throws CustomException the custom exception
     */
    public ValidationResponse validateVmwareInfo(VmwareInfo vmwareInfo) throws CustomException {
        String vmwareId;
        String ip = vmwareInfo.getIp();
        String username = vmwareInfo.getUsername();
        String password = vmwareInfo.getPassword();
        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new CustomException(ErrorCode.VMWARE_YML_EMPTY_CODE, ErrorCode.VMWARE_YML_EMPTY_MSG);
        }
        // ping IP
        if (!ipCanBeConnectedDiffIpVersion(ip, Constant.IPV4)) {
            logger.error("The IP is unreachable");
            throw new CustomException(ErrorCode.FAILED_CODE, ErrorCode.IP_UNREACHABLE_ERROR_MSG);
        }
        try {
            extendedAppUtil.getConnection().checkVmwareConnection("https://" + ip + "/sdk", username, password);
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("Check vmware connection failed.", e);
            throw new CustomException(ErrorCode.FAILED_CODE, ErrorCode.VMWARE_LOGIN_FAILED_ERROR_MSG);
        }
        vmwareId = generateUuid();
        return new ValidationResponse().setValidationResult(true).setVmwareId(vmwareId);
    }

    private static boolean verSionNotValid(String ipVersion) {
        return !Constant.IPV4.equals(ipVersion) && !Constant.IPV6.equals(ipVersion);
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        String version = null;
        try {
            Map<String, Object> versionMap = YamlUtil.getYamlMap("version.yml");
            if (MapUtils.isEmpty(versionMap)) {
                logger.warn("Version yml is empty");
                return null;
            }
            version = String.valueOf(versionMap.get("version"));
        } catch (Exception e) {
            logger.error("", e);
        }
        return version;
    }

    /**
     * Upload file.
     *
     * @param localFile the local file
     * @param uploadUri the upload uri
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyStoreException        the key store exception
     * @throws KeyManagementException   the key management exception
     * @throws IOException              the io exception
     */
    public static void uploadFile(File localFile, URI uploadUri)
        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
            sslContextBuilder.build());
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        HttpPut httpPut = new HttpPut(uploadUri);
        httpPut.setEntity(new FileEntity(localFile));
        HttpResponse httpResponse = client.execute(httpPut);
        EntityUtils.consumeQuietly(httpResponse.getEntity());
    }

    /**
     * 获取虚拟机配置类.
     *
     * @param svc the svc
     * @param vm the vm
     * @return the vm config info
     */
    public static Object getVmConfigInfo(ServiceUtil svc, ManagedObjectReference vm) {
        Object object = svc.getDynamicProperty(vm, "config");
        if (object instanceof VirtualMachineConfigInfo) {
            return object;
        } else {
            logger.error("GetVmConfigInfo ClassCastException vm");
            return null;
        }
    }
}
