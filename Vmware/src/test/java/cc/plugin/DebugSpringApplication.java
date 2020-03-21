/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class DebugSpringApplication extends SpringBootServletInitializer {
    private static Logger logger = LoggerFactory.getLogger(DebugSpringApplication.class);

    public static void main(String[] args) {
        try {
            List<String> argsList = new ArrayList<>();
            argsList.addAll(Arrays.asList(args));
            args = argsList.toArray(args);
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
            scanner.setResourceLoader(new PathMatchingResourcePatternResolver(Application.class.getClassLoader()));
            Set<BeanDefinition> candidates = scanner.findCandidateComponents("cc.plugin");
            String applicationClass = "";
            String contextClass = "";

            for (BeanDefinition bean : candidates) {

                MultiValueMap<String, Object> annotation
                    = (((ScannedGenericBeanDefinition) bean).getMetadata()).getAllAnnotationAttributes(
                    "org.springframework.boot.autoconfigure.SpringBootApplication");
                if (null != annotation) {
                    applicationClass = bean.getBeanClassName();
                    continue;
                }

                if (bean.getBeanClassName().contains("PropertyPasswordDecryptContextInitializer")) {
                    contextClass = bean.getBeanClassName();
                }
            }
            SpringApplication application = new SpringApplication(Class.forName(applicationClass));
            if (!contextClass.equals("")) {
                application.addInitializers(
                    (ApplicationContextInitializer<?>) Class.forName(contextClass).newInstance());
            }
            application.addInitializers(new ContextInitializer());
            application.run(args);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}


