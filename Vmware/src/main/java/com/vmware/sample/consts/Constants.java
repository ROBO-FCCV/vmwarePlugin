/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.consts;

import lombok.experimental.UtilityClass;

/**
 * Constants
 *
 * @since 2020-09-15
 */
@UtilityClass
public class Constants {
    /**
     * The constant ip v4 IP_RULE.
     */
    public final String IP_RULE = "(^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d"
        + "|[01]?\\d\\d?)$)|(^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]"
        + "\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|"
        + "^::([\\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)"
        + "\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:)"
        + ":([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\"
        + ".){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){2}"
        + ":([\\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.)"
        + "{3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){3}:"
        + "([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.)"
        + "{3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){4}:"
        + "((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]"
        + "?\\d\\d?)$|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$|^:((:[\\da"
        + "-fA-F]{1,4}){1,6}|:)$|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,"
        + "5}|:)$|^([\\da-fA-F]{1,4}:){2}((:[\\da-fA-F]{1,4}){1,4}|:)$|^(["
        + "\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)$|^([\\da-fA-F]{1,4}"
        + ":){4}((:[\\da-fA-F]{1,4}){1,2}|:)$|^([\\da-fA-F]{1,4}:){5}:"
        + "([\\da-fA-F]{1,4})?$|^([\\da-fA-F]{1,4}:){6}:$)";
    /**
     * ip4 regularity
     */
    public final String IPV4 = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    /**
     * UUID regularity
     */
    public final String ID_REGEXP = "^[0-9A-Fa-f]{32}$";

    /**
     * vm id
     */
    public final String VID_REGEXP = "vm-\\d+";

    /**
     * host id
     */
    public final String HID_REGEXP = "host-\\d+";

    /**
     * cluster id
     */
    public final String CID_REGEXP = "domain-[c|s]\\d+";

    /**
     * dataStore id
     */
    public final String DID_REGEXP = "datastore-\\d+";
    /**
     * Windows系统标识
     */
    public final String OS_WINDOWS = "WINDOWS";
    /**
     * Windows Workgroup 标识
     */
    public final String WINDOWS_WORKGROUP = "WORKGROUP";
    /**
     * local Domain
     */
    public final String LOCAL_DOMAIN = "LocalDomain";
    /**
     * 管理网卡
     */
    public final String MANAGE_TYPE_NETWORK = "manage";
}
