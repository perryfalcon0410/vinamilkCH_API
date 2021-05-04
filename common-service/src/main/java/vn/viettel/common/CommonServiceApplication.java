package vn.viettel.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaAuditing
@EnableFeignClients
@EnableSwagger2
@ComponentScan(basePackages = {"vn.viettel.common", "vn.viettel.core.handler", "vn.viettel.core.security", "vn.viettel.core.service"})
public class CommonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonServiceApplication.class, args);
	}

//	@Bean
//	public Docket swaggerApiV1() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.groupName("Common_api_V1")
//				.select()
//				.apis(RequestHandlerSelectors.basePackage("vn.viettel.common.controller"))
////				.paths(PathSelectors.any())
//				.paths(regex("/api/v1.*"))
//				.build()
//				.apiInfo(new ApiInfoBuilder().version("2.0").title("Common API").description("Documentation Common API v2.0").build())
//				.securitySchemes(Arrays.asList(securityScheme()))
//				.securityContexts(Arrays.asList(securityContexts()));
//	}

//	@Bean
//	public Docket swaggerApiV2() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.groupName("Common_api_V2")
//				.select()
//				.apis(RequestHandlerSelectors.basePackage("vn.viettel.common.controller"))
////				.paths(PathSelectors.any())
//				.paths(regex("/api/v2.*"))
//				.build()
//				.apiInfo(new ApiInfoBuilder().version("1.0").title("Common API").description("Documentation Common API v1.0").build())
//				.securitySchemes(Arrays.asList(securityScheme()))
//				.securityContexts(Arrays.asList(securityContexts()));
//	}
//
//	private SecurityContext securityContexts() {
//		return SecurityContext.builder()
//				.securityReferences(Arrays.asList(basicAuthReference()))
//				.forPaths(PathSelectors.any())
//				.build();
//	}
//
//	private SecurityScheme securityScheme() {
//		return new BasicAuth("basicAuth");
//	}
//
//	private SecurityReference basicAuthReference() {
//		return new SecurityReference("basicAuth", new AuthorizationScope[0]);
//	}
}
