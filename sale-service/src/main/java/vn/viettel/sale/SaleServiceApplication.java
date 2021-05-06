package vn.viettel.sale;

import com.google.common.base.Predicates;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableJpaAuditing
@EnableAsync
@EnableFeignClients
@EnableSwagger2
@EntityScan(value = {"vn.viettel.core.db.entity","vn.viettel.sale.entities"})
@ComponentScan(basePackages = {"vn.viettel.sale", "vn.viettel.core.handler", "vn.viettel.core.security", "vn.viettel.core.service"})
public class SaleServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaleServiceApplication.class, args);
	}

	@Bean
	public Docket swaggerApiV1() {
		return createDocket("1");
	}

	@Bean
	public Docket swaggerApiV2() {
		return createDocket("2");
	}

	private Docket createDocket(String version){
		String packageToscan = "vn.viettel.sale.controller";
		String serviceName = "Sale Service";

		return new Docket(DocumentationType.SWAGGER_2)
				.groupName(serviceName.toLowerCase().replace(" ", "_") + "_v" + version)
				.select()
				.apis(RequestHandlerSelectors.basePackage(packageToscan))
				.paths(PathSelectors.ant("/api/v" + version + "/**"))
				.build()
				.apiInfo(new ApiInfoBuilder().version(version).title(serviceName + " API").description("Documentation " + serviceName + " API V" + version).build())
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, getCustomizedResponseMessages())
//				.securitySchemes(Arrays.asList(new BasicAuth("basicAuth")))
				;
	}

	private List<ResponseMessage> getCustomizedResponseMessages(){
		List<ResponseMessage> responseMessages = new ArrayList<>();
		responseMessages.add(new ResponseMessageBuilder().code(500).message("Server has crashed!!").responseModel(new ModelRef("Error")).build());
		responseMessages.add(new ResponseMessageBuilder().code(403).message("You shall not pass!!").build());
		return responseMessages;
	}
}
