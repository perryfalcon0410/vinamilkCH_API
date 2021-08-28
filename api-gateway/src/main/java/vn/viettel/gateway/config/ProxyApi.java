package vn.viettel.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAutoConfiguration
public class ProxyApi {

    @Autowired
    ZuulProperties properties;

    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            List resources = new ArrayList();
//            resources.add(createResource("common_service_v1", "/common-service/v3/api-docs/common_service_v1", "3.0"));
//            resources.add(createResource("common_service_v2", "/common-service/v3/api-docs", "3.0"));
            properties.getRoutes().values().stream()
                    .forEach(route -> resources.add(createResource(route.getServiceId(), "/" + route.getId() + "/v2/api-docs", "2.0")));
            return resources;
        };
    }

    private SwaggerResource createResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}