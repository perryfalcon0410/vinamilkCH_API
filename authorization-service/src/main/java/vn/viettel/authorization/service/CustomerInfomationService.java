package vn.viettel.authorization.service;

import vn.viettel.authorization.messaging.customer.*;
import vn.viettel.authorization.service.dto.CardInfoDTO;
import vn.viettel.authorization.service.dto.customer.CustomerInfoReservationDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.authorization.messaging.customer.*;

import java.util.List;

public interface CustomerInfomationService extends BaseService {

    CustomerInfoReservationByPhoneResponse getCustomerInfoForReservationByPhone(CustomerInfoReservationByPhoneRequest request);

    CustomerInfomationResponse<CustomerInfoReservationDTO> findCustomerInfoReservationByCustomerNumber(CustomerInfoReservationByCustomerNumberRequest request);

    /* GET CARD INFORMATION OF USER */
    Response<CardInfoDTO> getCustomerCard();

    CustomerInfomationResponse<String> createCustomerCard(CustomerCardCreateRequest request);

    Response<List<Long>> getAllByCustomerName(String name);
}
