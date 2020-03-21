/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述
 *
 * @since 2019-09-21
 */
@Configuration
public class DozerBeanMapperConfigure {
    @Bean
    public Mapper mapper() {
        return DozerBeanMapperBuilder.buildDefault();
    }
}

