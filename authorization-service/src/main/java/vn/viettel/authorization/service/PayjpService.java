package vn.viettel.authorization.service;

import java.util.HashMap;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.dto.CardInfoDTO;

public interface PayjpService {

    /* CREATE CUSTOMER IN PAYJP */
    String createCustomer(String email);

    Response<String> createCardToken(HashMap<String, Object> card);

    Response<String> addCustomerCard(String customerId, HashMap<String, Object> card);

    Response<String> updateCustomerCard( String customerId, HashMap<String, Object> card);

    Response<CardInfoDTO> getCardInformation(String customerId);

}
