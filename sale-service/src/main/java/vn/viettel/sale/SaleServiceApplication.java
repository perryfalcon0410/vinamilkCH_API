package vn.viettel.sale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableJpaAuditing
@EnableAsync
@EntityScan(value = {"vn.viettel.core.db.entity","vn.viettel.sale.entities"})
@ComponentScan(basePackages = {"vn.viettel.sale", "vn.viettel.core.handler", "vn.viettel.core.security", "vn.viettel.core.service"})
public class SaleServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaleServiceApplication.class, args);
	}

}
