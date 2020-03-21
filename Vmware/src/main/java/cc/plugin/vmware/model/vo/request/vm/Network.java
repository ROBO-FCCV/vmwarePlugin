/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 虚拟网络实体
 *
 * @since 2019 -09-09
 */
public class Network {

    @ApiModelProperty(value = "掩码", example = "192.0.2.0", required = true)
    @NotEmpty
    private String netmask = "127.0.0.1";

    //
    // /**
    // * gateway
    // */
    @ApiModelProperty(value = "路由", example = "192.0.2.0", required = true)
    @NotEmpty
    private String gateway = "255.0.0.0";

    //
    // /**
    // * ip
    // */
    @ApiModelProperty(value = "ip", example = "192.0.2.0", required = true)
    @NotEmpty
    private String ipAddress = "127.0.0.1";

    @ApiModelProperty(value = "mac地址", example = "192.0.2.0", required = false)
    private String macAddress;

    /**
     * 交换机的UUID
     */
    @ApiModelProperty(value = "交换机的UUID", example = "69edffdfafd", required = false)
    private String switchUuid;

    /**
     * 资源的名称
     */
    @ApiModelProperty(value = "资源的名称", example = "name234", required = false)
    private String name;

    /**
     * 端口组的密钥。 如果指定，则此对象表示DistributedVirtualPortgroup和vNIC / pNIC之间的连接或关联。
     * 在这种情况下，对于早期绑定的端口组，不需要设置portKey，对于后期绑定的端口组，则不允许设置。 portKey属性将在端口绑定时由实现填充。
     */
    @ApiModelProperty(value = "portGroupUrn", required = true)
    @Valid
    private PortGroupUrn portGroupUrn;

    @ApiModelProperty(value = "")
    private List<String> dns;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型", example = "dvs", required = false)
    private String type;

    /**
     * Gets gateway.
     *
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * Sets gateway.
     *
     * @param gateway the gateway
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets ip address.
     *
     * @param ipAddress the ip address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets switch uuid.
     *
     * @return the switch uuid
     */
    public String getSwitchUuid() {
        return switchUuid;
    }

    /**
     * Sets switch uuid.
     *
     * @param switchUuid the switch uuid
     */
    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    /**
     * Gets dns.
     *
     * @return the dns
     */
    public List<String> getDns() {
        return dns;
    }

    /**
     * Sets dns.
     *
     * @param dns the dns
     */
    public void setDns(List<String> dns) {
        this.dns = dns;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets mac address.
     *
     * @return the mac address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets mac address.
     *
     * @param macAddress the mac address
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Gets port group urn.
     *
     * @return the port group urn
     */
    public PortGroupUrn getPortGroupUrn() {
        return portGroupUrn;
    }

    /**
     * Sets port group urn.
     *
     * @param portGroupUrn the port group urn
     */
    public void setPortGroupUrn(PortGroupUrn portGroupUrn) {
        this.portGroupUrn = portGroupUrn;
    }

    /**
     * Gets netmask.
     *
     * @return the netmask
     */
    public String getNetmask() {
        return netmask;
    }

    /**
     * Sets netmask.
     *
     * @param netmask the netmask
     */
    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }
}
