/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * filter配置
 *
 * @since 2019 -11-23
 */
@Configuration
public class FilterConfiguration {
    @Autowired
    private TraceIdFilter traceIdFilter;

    /**
     * Trace id filter registration filter registration bean.
     *
     * @return the filter registration bean
     */
    @Bean
    @Order(0)
    public FilterRegistrationBean traceIdFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(this.traceIdFilter);
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setName("traceIdFilter");
        return registration;
    }
}
