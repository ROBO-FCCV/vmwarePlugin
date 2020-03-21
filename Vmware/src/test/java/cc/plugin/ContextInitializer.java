/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;

@Configuration
public class ContextInitializer extends WebMvcConfigurerAdapter
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static Logger logger = LoggerFactory.getLogger(ContextInitializer.class);

    @Autowired
    ServletContext context;

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        CompositePropertySource zkSource = null;
        MapPropertySource decryptSource = null;
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource.getName().equals("bootstrapProperties") && propertySource != null) {
                zkSource = (CompositePropertySource) propertySource;
                propertySources.remove("bootstrapProperties");
                propertySources.addBefore("defaultProperties", zkSource);
            }
            if (propertySource.getName().equals("decryptbootstrapProperties") && propertySource != null) {
                decryptSource = (MapPropertySource) propertySource;
                propertySources.remove("decryptbootstrapProperties");
                propertySources.addBefore("defaultProperties", decryptSource);
            }
        }
    }

    //@Bean
    public FilterRegistrationBean copyValidateFiles() {
        String docPath = this.context.getRealPath("");
        File testRoot = new File(String.valueOf(this.getClass().getResource("/")));
        String webINFFolder = testRoot.getParentFile().getParentFile().getPath().substring(6)
            + "\\src\\main\\webapp\\WEB-INF";
        copyFolder(webINFFolder, docPath + "WEB-INF");

        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new CommonsRequestLoggingFilter());
        return filter;
    }

    public void copyFolder(String srcPath, String dstPath) {

        try {
            (new File(dstPath)).mkdirs();
            String[] files = new File(srcPath).list();
            AtomicReference<File> temp = new AtomicReference<>();
            Arrays.stream(files).forEach(f -> {
                temp.set(getOldPath(srcPath, f));
                try {
                    copyFile(dstPath, temp.get());
                    if (temp.get().isDirectory()) {
                        copyFolder(srcPath + "/" + f, dstPath + "/" + f);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getOldPath(String srcPath, String fileName) {
        File temp;
        if (srcPath.endsWith(File.separator)) {
            temp = new File(srcPath + fileName);
        } else {
            temp = new File(srcPath + File.separator + fileName);
        }
        return temp;
    }

    public void copyFile(String dstPath, File srcFile) throws IOException {
        if (srcFile.isFile()) {
            FileInputStream input = new FileInputStream(srcFile);
            FileOutputStream output = new FileOutputStream(dstPath + "/" + (srcFile.getName()));
            byte[] b = new byte[1024 * 10];
            int len;
            while ((len = input.read(b)) != -1) {
                output.write(b, 0, len);
            }
            output.flush();
            output.close();
            input.close();
        }
    }
}