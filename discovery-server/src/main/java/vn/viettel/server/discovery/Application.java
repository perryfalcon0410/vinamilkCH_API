/*
 * Copyright (c) 2018 lit-inc.jp
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Zwoop.biz
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in  accordance with the terms of the
 * license agreement you entered into with LIT.
 */
package vn.viettel.server.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
