package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.messaging.customer.*;
import vn.viettel.authorization.messaging.customer.*;
import vn.viettel.authorization.repository.CustomerInfomationRepository;
import vn.viettel.authorization.service.CustomerInfomationService;
import vn.viettel.authorization.service.PayjpService;
import vn.viettel.authorization.service.RoleService;
import vn.viettel.authorization.service.UserService;
import vn.viettel.authorization.service.dto.CardInfoDTO;
import vn.viettel.authorization.service.dto.customer.CustomerInfoReservationDTO;
import vn.viettel.authorization.service.dto.customer.CustomerInformationDTO;
import vn.viettel.core.db.entity.CustomerInformation;
import vn.viettel.core.db.entity.Role;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.service.mapper.CustomModelMapper;
import vn.viettel.core.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerInfomationServiceImpl extends BaseServiceImpl<CustomerInformation, CustomerInfomationRepository> implements CustomerInfomationService {

    @Autowired
    CustomModelMapper modelMapper;

    @Autowired
    UserService userService;

    @Autowired
    PayjpService payjpService;

    @Autowired
    RoleService roleService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * get customer information for reservation by phone of customer
     */
    @Override
    public CustomerInfoReservationByPhoneResponse getCustomerInfoForReservationByPhone(CustomerInfoReservationByPhoneRequest request) {
        CustomerInfoReservationByPhoneResponse response = new CustomerInfoReservationByPhoneResponse();

        CustomerInformation customerInformation = repository.findByPhone(request.getPhone());
        if (customerInformation == null) {
            response.setFailure(ResponseMessage.CUSTOMER_INFORMATION_DOES_NOT_EXIST);
        } else {
            // map data to response
            CustomerInfoReservationDTO resData = new CustomerInfoReservationDTO();
            resData.setCustomerId(customerInformation.getId());
            // resData.setCustomerNumber(customerInformation.getCustomerNumber().getCustomerNumber());
            resData.setName(customerInformation.getName());
            response.setData(resData);
        }
        return response;
    }

    /**
     * find by customer number
     */
    @Override
    public CustomerInfomationResponse<CustomerInfoReservationDTO> findCustomerInfoReservationByCustomerNumber(CustomerInfoReservationByCustomerNumberRequest request) {
        CustomerInfomationResponse<CustomerInfoReservationDTO> response = new CustomerInfomationResponse<CustomerInfoReservationDTO>();

        Optional<CustomerInformation> optCustomerInformation = repository.findByCustomerNumber(request.getCustomerNumber());
        if (!optCustomerInformation.isPresent()) {
            response.setFailure(ResponseMessage.CUSTOMER_NUMBER_DOES_NOT_EXIST);
            return response;
        }

        CustomerInformation customerInformation = optCustomerInformation.get();
        CustomerInfoReservationDTO resData = modelMapper.map(customerInformation, CustomerInfoReservationDTO.class);
        resData.setEmail(customerInformation.getUser().getEmail());
        response.setData(resData);
        return response;
    }

    /* GET CARD INFORMATION OF USER */
    /* RETURN NULL IF NOT EXIST */
    @Override
    public Response<CardInfoDTO> getCustomerCard() {
        CustomerInformation customerInformation = repository.findByUserId(getUserId());
        if (customerInformation == null) {
            Response<CardInfoDTO> response = new Response<CardInfoDTO>();
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        String customerId = customerInformation.getPayjpCustomerId();
        return payjpService.getCardInformation(customerId);
    }

    @Override
    public CustomerInfomationResponse<String> createCustomerCard(CustomerCardCreateRequest request) {
        CustomerInformation customerInformation = repository.findByUserId(getUserId());
        if (customerInformation == null) {
            Response<CardInfoDTO> response = new Response<CardInfoDTO>();
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        String customerId = customerInformation.getPayjpCustomerId();

        logger.info("==> Add card to Payjp customer id:" + customerId);

        HashMap<String, Object> cardParams = new HashMap<>();
        cardParams.put("name", request.getCardName());
        cardParams.put("number", request.getCardNumber());
        cardParams.put("cvc", request.getCvv());
        cardParams.put("exp_month", request.getExpMonth());
        cardParams.put("exp_year", request.getExpYear());

        Response<String> cardResponse = new Response<String>();

        if (customerId == null || customerId.isEmpty()) {
            CustomerInformationDTO resData = modelMapper.map(customerInformation, CustomerInformationDTO.class);
            customerId = payjpService.createCustomer(resData.getUser().getEmail());
            // Update payjp customer Id to customer_information
            customerInformation.setPayjpCustomerId(customerId);
            repository.save(customerInformation);
            cardResponse = payjpService.addCustomerCard(customerId, cardParams);
        } else {
            cardResponse = payjpService.updateCustomerCard(customerId, cardParams);
        }

        CustomerInfomationResponse<String> response = new CustomerInfomationResponse<String>();
        response.setSuccess(cardResponse.getSuccess());
        response.setStatusCode(cardResponse.getStatusCode());
        response.setStatusValue(cardResponse.getStatusValue());
        response.setData(cardResponse.getData());
        return response;
    }

    @Override
    public Response<List<Long>> getAllByCustomerName(String name) {
        Optional<List<CustomerInformation>> customerInformationList = repository.findAllByCustomerName(name);
        List<Long> result = new ArrayList<>();
        if (customerInformationList.isPresent()) {
            for (CustomerInformation cus : customerInformationList.get()) {
                result.add(cus.getUser().getId());
            }
        }
        return new Response<List<Long>>().withData(result);
    }

    /* ==================== PRIVATE METHOD ==================== */
    /* GET CUSTOMER ROLE */
    private Role getCustomerRole() {
        return roleService.getByRoleName(UserRole.CUSTOMER);
    }
}
