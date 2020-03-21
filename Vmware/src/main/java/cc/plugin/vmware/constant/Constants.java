/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.constant;

/**
 * 功能描述
 *
 * @since 2019 -09-23
 */
public class Constants {
    /**
     * vnc端口
     */
    public static final String STATUS_OK = "ok";

    /**
     * 虚拟IDE控制器key
     */
    public static final int VIRTUAL_IDE_CONTROLLER_KEY = 200;

    /**
     * 虚拟CDROM key
     */
    public static final int VIRTUAL_CDROM_KEY = 3002;

    /**
     * 虚拟SCCI控制器key
     */
    public static final int PARA_VIRTUAL_SCSI_CONTROLLER_KEY = 1000;

    /**
     * 虚拟磁盘key
     */
    public static final int VIRTUAL_DISK_KEY = 2000;

    /**
     * 虚拟磁盘unitNumber默认值
     */
    public static final int DEFAULT_VIRTUAL_DISK_UNIT_NUMBER = 0;

    /**
     * 每个插槽的内核数默认值
     */
    public static final int DEFAULT_NUM_CORES_PER_SOCKET = 1;

    /**
     * vnc可用
     */
    public static final String VNC_ENABLED = "remoteDisplay.vnc.enabled";

    /**
     * vnc端口
     */
    public static final String VNC_PORT = "remoteDisplay.vnc.port";

    /**
     * vnc密码
     */
    public static final String VNC_PWD = "remoteDisplay.vnc.password";

    /**
     * vnc密码长度为8
     */
    public static final int VNC_PASSWORD_LENGTH_EIGHT = 8;

    /**
     * windows系统
     */
    public static final String OS_TYPE_WINDOWS = "WINDOWS";

    /**
     * linux系统
     */
    public static final String OS_TYPE_LINUX = "LINUX";

    /**
     * 其他系统
     */
    public static final String OS_TYPE_OTHER = "OTHER";
}
