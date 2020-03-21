/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = "enabled", prefix = "swagger", havingValue = "true", matchIfMissing = false)
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar
            .name("X-AUTH-TOKEN")
            .description("token")
            .modelRef(new ModelRef("string"))
            .parameterType("header")
            .required(true)
            .build();
        pars.add(ticketPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
            .globalOperationParameters(pars)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("cc.plugin"))
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("vmware api").description("vmware接口文档说明").version("1.0").build();
    }

    @Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration(null, "list", "alpha", "schema", UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS,
            false, true, 60000L);
    }

}