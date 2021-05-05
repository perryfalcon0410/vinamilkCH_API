package vn.viettel.common;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class ConfigSwagger {

    @Bean
    public GroupedOpenApi groupApiV1() {
        String paths[] = {"/api/v1/**"};
        String packagesToscan[] = {"vn.viettel.common.controller"};
        return GroupedOpenApi.builder().group("common_service_v1").pathsToMatch(paths).packagesToScan(packagesToscan)
                .build();
    }

    @Bean
    public GroupedOpenApi groupApiV2() {
        String paths[] = {"/api/v2/**"};
        String packagesToscan[] = {"vn.viettel.common.controller"};
        return GroupedOpenApi.builder().group("common_service_v2").pathsToMatch(paths).packagesToScan(packagesToscan)
                .build();
    }
}
