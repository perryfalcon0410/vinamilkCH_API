/*
 * Copyright 2021 Viettel Business Solutions. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package vn.viettel.core.jms;

import java.util.List;

import javax.jms.DeliveryMode;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import lombok.extern.slf4j.Slf4j;
import vn.viettel.core.util.StringUtils;

@Slf4j
public class JMSSender {

	private static final String COMMA = ",";

	private static final String DEFAULT_DESTINATION_QUEUE_NAME = "integration_queue";

	private ActiveMQConnectionFactory factory;

	private JmsTemplate jmsTemplate;

	public JMSSender(String username, String password, String url) {
		try {
			//password = StringUtil.decryptAES(password);
			if (StringUtils.stringNotNullOrEmpty(url) && StringUtils.stringNotNullOrEmpty(username)) {
				factory = new ActiveMQConnectionFactory(username, password, url);
				jmsTemplate = new JmsTemplate(factory);
			}
		} catch (Exception ex) {
			log.error("khoi tao jmsSender", ex);
		}
	}

	public void sendMessage(final String type, List<Long> listId) {
		if (null != jmsTemplate) {
			StringBuilder sb = new StringBuilder();
			for (Long id : listId) {
				if (sb.length() > 0) {
					sb.append(COMMA);
				}
				sb.append(id);
			}
			jmsTemplate.convertAndSend(DEFAULT_DESTINATION_QUEUE_NAME, sb.toString(), m -> {
					m.setJMSType(type);
					m.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
					return m;
			});
		}
	}
	
	public void sendMessageByCode(final String type, List<String> lstCode) {
		StringBuilder sb = new StringBuilder();
		for (String id : lstCode) {
			if (sb.length() > 0) {
				sb.append(COMMA);
			}
			sb.append(id);
		}
		jmsTemplate.convertAndSend(DEFAULT_DESTINATION_QUEUE_NAME, sb.toString(), m -> {
				m.setJMSType(type);
				m.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
				return m;
		});
	}

}