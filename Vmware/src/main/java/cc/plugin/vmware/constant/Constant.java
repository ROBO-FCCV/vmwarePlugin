/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.constant;

/**
 * 功能描述
 *
 * @since 2019-09-25
 */
public interface Constant {
    /**
     * The constant EXPIRED_TIME.
     */
    long EXPIRED_TIME = 10 * 60 * 1000;

    /**
     * The constant TOKEN_MAXIMUM.
     */
    long TOKEN_MAXIMUM = 1000;

    /**
     * The constant encryptLocation.
     */
    String encryptLocation = "/tomcat/webapps/vmware/WEB-INF/classes/script/KMCEncrypt"
        + ".py";

    /**
     * The constant decryptLocation.
     */
    String decryptLocation = "/tomcat/webapps/vmware/WEB-INF/classes/script/KMCDecrypt"
        + ".py";

    /**
     * The constant IPV4.
     */
    String IPV4 = "4";

    /**
     * The constant IPV6.
     */
    String IPV6 = "6";

    /**
     * The constant SINGLE_PING_IP_TIMES.
     */
    int SINGLE_PING_IP_TIMES = 3;

    /**
     * The constant SINGLE_PING_IP_LINUX_TIME_OUT.
     */
    int SINGLE_PING_IP_LINUX_TIME_OUT = 5;

    /**
     * The constant CHARSET_UTF8.
     */
    String CHARSET_UTF8 = "UTF-8";

    /**
     * The constant IP_RULE.
     */
    String IP_RULE = "(^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d"
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
     * The constant ID_REGEXP.
     */
    String ID_REGEXP = "^[0-9A-Za-z]{1,64}$";

    /**
     * The constant TASK.
     */
    String TASK = "Task";

    /**
     * The constant INFO.
     */
    String INFO = "info";

    /**
     * The constant VIRTUAL_MACHINE.
     */
    String VIRTUAL_MACHINE = "VirtualMachine";
}
