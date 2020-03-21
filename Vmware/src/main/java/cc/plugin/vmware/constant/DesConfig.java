/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.constant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述
 *
 * @since 2019 -10-09
 */
@Configuration
public class DesConfig implements InitializingBean {
    @Value("${desKey.key1}")
    private String key1;

    @Value("${desKey.key2}")
    private String key2;

    @Value("${desKey.key3}")
    private String key3;

    @Override
    public void afterPropertiesSet() {

    }

    /**
     * Gets key 1.
     *
     * @return the key 1
     */
    public String getKey1() {
        return key1;
    }

    /**
     * Gets key 2.
     *
     * @return the key 2
     */
    public String getKey2() {
        return key2;
    }

    /**
     * Gets key 3.
     *
     * @return the key 3
     */
    public String getKey3() {
        return key3;
    }

}
