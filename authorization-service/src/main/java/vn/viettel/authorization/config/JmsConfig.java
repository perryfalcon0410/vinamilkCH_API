/*
 * Copyright 2021 Viettel Business Solutions. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package vn.viettel.authorization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.viettel.core.jms.JMSSender;

@Configuration
public class JmsConfig {

	@Value("${activemq-url}")
	private String url;

	@Value("${activemq-username}")
	private String username;

	@Value("${activemq-password}")
	private String password;

	@Bean
	JMSSender jmsSender() {
		return new JMSSender(username, password, url);
	}

}