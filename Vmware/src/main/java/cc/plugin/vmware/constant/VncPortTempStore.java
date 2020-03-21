/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * 功能描述
 *
 * @since 2019 -09-26
 */
public class VncPortTempStore {
    // 已经自行实例化
    private static final VncPortTempStore VNC_PORT_TEMP_STORE = new VncPortTempStore();

    private Set<Integer> ports = new HashSet<Integer>();

    // 私有的默认构造子
    private VncPortTempStore() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VncPortTempStore getInstance() {
        return VNC_PORT_TEMP_STORE;
    }

    /**
     * Gets port set.
     *
     * @return the port set
     */
    public Set<Integer> getPortSet() {
        return ports;
    }

    /**
     * 增加端口
     *
     * @param port port
     */
    public void putPortSet(int port) {
        ports.add(port);
    }

    /**
     * 删除端口
     *
     * @param port port
     */
    public void removePortSet(int port) {
        boolean hasPort = ports.contains(port);
        if (hasPort) {
            ports.remove(port);
        }
    }
}
