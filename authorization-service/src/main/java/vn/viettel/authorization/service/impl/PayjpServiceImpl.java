package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.service.PayjpService;
import vn.viettel.authorization.service.dto.CardInfoDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.PayjpErrorMessage;
import vn.viettel.core.ResponseMessage;
import jp.pay.Payjp;
import jp.pay.exception.CardException;
import jp.pay.exception.PayjpException;
import jp.pay.model.Card;
import jp.pay.model.Customer;
import jp.pay.model.Token;
import jp.pay.net.RequestOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayjpServiceImpl implements PayjpService {

	private Logger logger = LoggerFactory.getLogger(getClass());
    private String FORMAT_CARD_NUMBER_HIDEND = "**** **** **** %s";

    @Value("${payjp.private-key}")
    private String PAYJP_PRIVATE_KEY;

    @Value("${payjp.public-key}")
    private String PAYJP_PUBLIC_KEY;

    /* CONFIG PAYJP */
    @PostConstruct
    private void init() {
        Payjp.apiKey = PAYJP_PRIVATE_KEY;
    }

    /* CREATE CUSTOMER */
    @Override
    public String createCustomer(String email) {
        String customerId = "";
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("email", email);
            param.put("description", "Creating PayJP Customer");
            customerId = Customer.create(param).getId();
        } catch (PayjpException e) {
            e.printStackTrace();
        }
        return customerId;
    }

    @Override
    public Response<String> addCustomerCard(String customerId, HashMap<String, Object> card) {
        Response response = new Response();
        if (StringUtils.isBlank(customerId)) {
            logger.info("== Payjp customer id is blank");
            response.setFailure(ResponseMessage.UNKNOWN);
            return response;
        }

        Response<String> tokenResponse = createCardToken(card);
        if (!tokenResponse.getSuccess()) {
            return tokenResponse;
        }

        String token = tokenResponse.getData();
        HashMap<String, Object> params = new HashMap<>();
        params.put("card", token);
        params.put("default", true);

        try {
            Customer customer = Customer.retrieve(customerId);
            if (customer.getDefaultCard() == null) {
            	response.setData(customer.createCard(params));
            } else {
                response.setFailure(ResponseMessage.CUSTOMER_CARD_HAS_EXIST);
            }
        } catch (PayjpException e) {
            response.setFailure(ResponseMessage.UNKNOWN.statusCode(), e.getMessage());
            logger.info("== ==================== == ");
            logger.info("=== message: {}", e.getMessage());
            logger.info("== ==================== == ");
        }
        return response;
    }

    @Override
    public Response<String> updateCustomerCard(String customerId, HashMap<String, Object> card) {
    	deleteCustomerCard(customerId);
        return addCustomerCard(customerId, card);
    }

    @Override
    public Response<CardInfoDTO> getCardInformation(String customerId) {
        Response<CardInfoDTO> response = new Response<>();

        if (StringUtils.isBlank(customerId)) {
            logger.info("== Payjp customer id is blank");
            response.setFailure(ResponseMessage.UNKNOWN);
            return response;
        }

        try {
            Customer customer = Customer.retrieve(customerId);
            String defaultCardId = customer.getDefaultCard();
            Card card = customer.getCards().retrieve(defaultCardId);

            CardInfoDTO cardInfo = new CardInfoDTO();
            cardInfo.setCardType(card.getBrand());
            cardInfo.setCardHolderName(card.getName());
            cardInfo.setCardNumber(doFormatCardNumberHidden(card.getLast4()));
            cardInfo.setExpMonth(card.getExpMonth());
            cardInfo.setExpYear(card.getExpYear());
            cardInfo.setCvv("***");
            response.setData(cardInfo);
        } catch (PayjpException e) {
            response.setFailure(ResponseMessage.UNKNOWN.statusCode(), e.getMessage());
            logger.info("== ==================== == ");
            logger.info("=== message: {}", e.getMessage());
            logger.info("== ==================== == ");
        }
        return response;
    }

    @Override
    public Response<String> createCardToken(HashMap<String, Object> card) {
        Response<String> response = new Response<>();

        HashMap<String, String> header = new HashMap<>();
        header.put("X-Payjp-Direct-Token-Generate", "true");
        RequestOptions options = RequestOptions.builder()
            .clearApiKey()
            .setApiKey(PAYJP_PUBLIC_KEY)
            .setAdditionalHeaders(header)
            .build();

        HashMap<String, Object> params = new HashMap<>();
        params.put("card", card);

        String token = null;
        try {
            token = Token.create(params, options).getId();
        } catch (CardException e) {
            logger.info("== ==================== == ");
            logger.info("=== code: {} - message: {}", e.getCode(), e.getMessage());
            logger.info("== ==================== == ");
            response.setFailure(ResponseMessage.UNKNOWN.statusCode(), e.getMessage());

            ResponseMessage error = PayjpErrorMessage.getErrorByCode(e.getCode());
            if (error != null) {
                response.setFailure(error);
            }
        } catch (PayjpException e) {
            response.setFailure(ResponseMessage.UNKNOWN.statusCode(), e.getMessage());
            logger.info("== ==================== == ");
            logger.info("=== message: ", e.getMessage());
            logger.info("== ==================== == ");
        }
        response.setData(token);
        return response;
    }

    private void deleteCustomerCard(String customerId) {
        try {
            String defaultCardId = Customer.retrieve(customerId).getDefaultCard();
            Customer.retrieve(customerId).getCards().retrieve(defaultCardId).delete();
        } catch (PayjpException e) {
            logger.info("== ==================== == ");
            logger.info("=== message: ", e.getMessage());
            logger.info("== ==================== == ");
        }
    }

    private String doFormatCardNumberHidden(String last4) {
        return String.format(FORMAT_CARD_NUMBER_HIDEND, last4);
    }
}
