/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

/**
 * The type Yaml util.
 *
 * @since 2019 -09-19
 */
public class YamlUtil {
    private static final Logger logger = LoggerFactory.getLogger(YamlUtil.class);

    /**
     * Gets yaml map.
     *
     * @param yaml the yaml
     * @return the yaml map
     */
    public static Map<String, Object> getYamlMap(String yaml) {
        YamlMapFactoryBean loginYaml = new YamlMapFactoryBean();
        try {
            loginYaml.setResources(new ClassPathResource(yaml));
        } catch (Exception e) {
            logger.error("Cannot read {}", yaml, e);
        }
        return loginYaml.getObject();
    }
}
