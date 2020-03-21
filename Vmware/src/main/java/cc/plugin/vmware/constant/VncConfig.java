/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.constant;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述
 *
 * @since 2019 -09-26
 */
@Configuration
public class VncConfig implements InitializingBean {
    @Value("${vmware.vnc.ip}")
    private String ip;

    @Value("${vmware.vnc.port}")
    private String port;

    @Value("${vmware.vnc.page}")
    private String page;

    @Value("${vmware.vnc.portMin}")
    private String portMin;

    @Value("${vmware.vnc.portMax}")
    private String portMax;

    @Value("${vmware.network.mapping}")
    private String networkMapping;

    @Value("${vmware.network.enabled}")
    private String networkEnabled;

    private String novncUrl;

    private JSONObject networkMap;

    @Override
    public void afterPropertiesSet() {
        novncUrl = initNovncUrl();
        networkMap = initNetWorkMap();
    }

    private String initNovncUrl() {
        StringBuilder sb = new StringBuilder("http://");
        sb.append(ip);
        if (!StringUtils.equals("80", port)) {
            sb.append(":").append(port);
        }
        sb.append("/").append(page);
        return sb.toString();
    }

    private JSONObject initNetWorkMap() {
        // 将配置文件的字符串转换成json可识别字符串
        networkMapping = networkMapping.replace(",", ":");
        networkMapping = networkMapping.replace(";", ",");
        networkMapping = networkMapping.replace("(", "{");
        networkMapping = networkMapping.replace(")", "}");
        JSONObject jsonObject = JSONObject.parseObject(networkMapping);
        return jsonObject;
    }

    /**
     * url
     *
     * @return novnc url
     */
    public String getNovncUrl() {
        if (StringUtils.isEmpty(novncUrl)) {
            novncUrl = initNovncUrl();
        }
        return novncUrl;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * Gets page.
     *
     * @return the page
     */
    public String getPage() {
        return page;
    }

    /**
     * Gets port min.
     *
     * @return the port min
     */
    public String getPortMin() {
        return portMin;
    }

    /**
     * Gets port max.
     *
     * @return the port max
     */
    public String getPortMax() {
        return portMax;
    }

    /**
     * Gets network map.
     *
     * @return the network map
     */
    public JSONObject getNetworkMap() {
        return networkMap;
    }

    /**
     * Gets network map enabled.
     *
     * @return the network map enabled
     */
    public boolean getNetworkMapEnabled() {
        return Boolean.parseBoolean(networkEnabled);
    }
}
