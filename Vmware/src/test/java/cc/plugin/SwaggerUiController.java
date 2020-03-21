/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static springfox.documentation.swagger.common.HostNameProvider.componentsFrom;

import com.github.xiaoymin.swaggerbootstrapui.model.SwaggerBootstrapUiPathInstance;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiSort;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.models.SwaggerBootstrapUi;
import io.swagger.models.SwaggerBootstrapUiPath;
import io.swagger.models.SwaggerBootstrapUiTag;
import io.swagger.models.SwaggerExt;
import io.swagger.models.properties.Property;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UriComponents;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller
@ApiIgnore
public class SwaggerUiController {

    /***
     * sort排序接口
     */
    public static final String DEFAULT_SORT_URL = "/v2/api-docs-ext";

    private static final String HAL_MEDIA_TYPE = "application/hal+json";
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerUiController.class);
    private final ServiceModelToSwagger2Mapper mapper;
    private final DocumentationCache documentationCache;
    private final JsonSerializer jsonSerializer;
    private final String hostNameOverride;

    @Autowired
    public SwaggerUiController(Environment environment, ServiceModelToSwagger2Mapper mapper,
        DocumentationCache documentationCache, JsonSerializer jsonSerializer) {
        this.mapper = mapper;
        this.documentationCache = documentationCache;
        this.jsonSerializer = jsonSerializer;
        this.hostNameOverride = environment.getProperty("springfox.documentation.swagger.v2.host", "DEFAULT");
    }

    @RequestMapping(value = DEFAULT_SORT_URL, method = RequestMethod.GET, produces = {
        APPLICATION_JSON_VALUE, HAL_MEDIA_TYPE
    })
    @ResponseBody
    public ResponseEntity<Json> apiSorts(@RequestParam(value = "group", required = false) String swaggerGroup,
        HttpServletRequest request) {
        String groupName = Optional.fromNullable(swaggerGroup).or(Docket.DEFAULT_GROUP_NAME);
        Documentation documentation = documentationCache.documentationByGroup(groupName);
        if (documentation == null) {
            LOGGER.warn("Unable to find specification for grouRp {}", groupName);
            return new ResponseEntity<Json>(HttpStatus.NOT_FOUND);
        }
        Swagger swagger = mapper.mapDocumentation(documentation);
        UriComponents uriComponents = componentsFrom(request, swagger.getBasePath());
        swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
        if (isNullOrEmpty(swagger.getHost())) {
            swagger.host(hostName(uriComponents));
        }
        // 扩展
        swagger = extend(swagger);
        SwaggerExt swaggerExt = new SwaggerExt(swagger);
        // swaggerBootstrapUi.setTagSortLists(getSortTag(request,documentation));
        swaggerExt.setSwaggerBootstrapUi(initSwaggerBootstrapUi(request, documentation));
        // Method 层排序
        return new ResponseEntity<Json>(jsonSerializer.toJson(swaggerExt), HttpStatus.OK);
    }

    @SuppressWarnings("deprecation")
    private SwaggerBootstrapUi initSwaggerBootstrapUi(HttpServletRequest request, Documentation documentation) {
        SwaggerBootstrapUi swaggerBootstrapUi = new SwaggerBootstrapUi();
        WebApplicationContext wc = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        Iterator<Tag> tags = documentation.getTags().iterator();
        List<SwaggerBootstrapUiTag> targetTagLists = Lists.newArrayList();
        // Ctl层排序
        Map<String, Object> beansWithAnnotation = wc.getBeansWithAnnotation(Controller.class);
        // path排序
        List<SwaggerBootstrapUiPath> targetPathLists = Lists.newArrayList();
        while (tags.hasNext()) {
            Tag sourceTag = tags.next();
            String tagName = sourceTag.getName();
            boolean exists = false;
            Class<?> aClass = null;
            Api api = null;
            for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
                aClass = entry.getValue().getClass();
                api = aClass.getAnnotation(Api.class);
                if (api != null) {
                    // 是否相等
                    if (Lists.newArrayList(api.tags()).contains(tagName)) {
                        exists = true;
                        break;
                    }
                }
            }
            // 获取order值
            int order = Integer.MAX_VALUE;
            SwaggerBootstrapUiTag tag = new SwaggerBootstrapUiTag(order);
            tag.name(sourceTag.getName()).description(sourceTag.getDescription());
            if (exists) {
                // 优先获取api注解的position属性,如果不等于0,则取此值,否则获取apiSort注解,判断是否为空,如果不为空,则获取apisort的值,
                // 优先级:@Api-position>@ApiSort-value
                int post = api.position();
                if (post == 0) {
                    ApiSort annotation = aClass.getAnnotation(ApiSort.class);
                    if (annotation != null) {
                        order = annotation.value();
                    }
                } else {
                    order = post;
                }
                // targetTagLists.add(new
                // Tag(sourceTag.getName(),sourceTag.getDescription(),order,sourceTag.getVendorExtensions()));
                tag.setOrder(order);
                // 获取父级path
                String parentPath = "";
                RequestMapping parent = aClass.getAnnotation(RequestMapping.class);
                if (parent != null) {
                    parentPath = parent.value()[0];
                }
                Method[] methods = aClass.getDeclaredMethods();
                for (Method method : methods) {
                    List<SwaggerBootstrapUiPath> paths = new SwaggerBootstrapUiPathInstance(parentPath, method).match();
                    if (paths != null && paths.size() > 0) {
                        targetPathLists.addAll(paths);
                    }
                }
            }
            targetTagLists.add(tag);

        }
        Collections.sort(targetTagLists, new Comparator<SwaggerBootstrapUiTag>() {
            @Override
            public int compare(SwaggerBootstrapUiTag o1, SwaggerBootstrapUiTag o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });
        Collections.sort(targetPathLists, new Comparator<SwaggerBootstrapUiPath>() {
            @Override
            public int compare(SwaggerBootstrapUiPath o1, SwaggerBootstrapUiPath o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        swaggerBootstrapUi.setTagSortLists(targetTagLists);
        swaggerBootstrapUi.setPathSortLists(targetPathLists);
        return swaggerBootstrapUi;
    }

    private String hostName(UriComponents uriComponents) {
        if ("DEFAULT".equals(hostNameOverride)) {
            String host = uriComponents.getHost();
            int port = uriComponents.getPort();
            if (port > -1) {
                return String.format("%s:%d", host, port);
            }
            return host;
        }
        return hostNameOverride;
    }

    private Swagger extend(Swagger swagger) {
        Iterator<Map.Entry<String, Model>> it = swagger.getDefinitions().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Model> entry = it.next();
            Model model = entry.getValue();
            String key = entry.getKey();
            if (key.contains("JsonResult")) {
                Map<String, Property> props = model.getProperties();
                Property dataProp = props.get("data");
                Property newProp = SwaggerUtil.getNewProp(dataProp, SwaggerUtil.getRealType(key),
                    swagger.getDefinitions());
                props.put("data", newProp);
            }
        }
        return swagger;
    }

}
