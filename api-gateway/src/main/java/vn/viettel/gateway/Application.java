package vn.viettel.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
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

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableZuulProxy
@EnableSwagger2
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Bean
//	public Docket api() {
//
//		SecurityReference securityReference = SecurityReference.builder()
//				.reference("basicAuth")
//				.scopes(new AuthorizationScope[0])
//				.build();
//
//		ArrayList<SecurityReference> reference = new ArrayList<>(1);
//		reference.add(securityReference);
//
//		ArrayList<SecurityContext> securityContexts = new ArrayList<>(1);
//		securityContexts.add(SecurityContext.builder().securityReferences(reference).build());
//
//		ArrayList<SecurityScheme> auth = new ArrayList<>(1);
//		auth.add(new BasicAuth("basicAuth"));
//
//		return new Docket(DocumentationType.SWAGGER_2)
////				.securitySchemes(auth)
////				.securityContexts(securityContexts)
//				.select()
////				.apis(RequestHandlerSelectors.basePackage("com.my.package.directory"))
//				.paths(PathSelectors.any())
//				.build()
//				.securityContexts(Arrays.asList(actuatorSecurityContext()))
//				.securitySchemes(Arrays.asList(basicAuthScheme()));
////				.apiInfo(getApiInfo());
//		}
//
//		private SecurityContext actuatorSecurityContext() {
//			return SecurityContext.builder()
//					.securityReferences(Arrays.asList(basicAuthReference()))
//					.forPaths(PathSelectors.ant("/api/**"))
//					.build();
//		}
//
//		private SecurityScheme basicAuthScheme() {
//			return new BasicAuth("basicAuth");
//		}
//
//		private SecurityReference basicAuthReference() {
//			return new SecurityReference("basicAuth", new AuthorizationScope[0]);
//		}
}
