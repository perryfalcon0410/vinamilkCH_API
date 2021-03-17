package vn.viettel.customer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.*;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.SearchCustomerService;
import vn.viettel.customer.service.dto.CustomerResponse;
import vn.viettel.customer.service.feign.AddressClient;
import vn.viettel.customer.service.feign.UserClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchCustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRepository> implements SearchCustomerService {

    @Autowired
    CustomerRepository cusRepo;

    @Autowired
    IDCardRepository idCardRepo;

    @Autowired
    CardMemberRepository memCardRepo;

    @Autowired
    CompanyRepository comRepo;

    @Autowired
    FullAddressRepository addressRepo;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    FullAddressRepository fullAddRepo;

    @Autowired
    UserClient userClient;

    @Autowired
    AddressClient addressClient;

    @Autowired
    CustomerService customerService;

    @Override
    public Response<List<CustomerResponse>> searchCustomer(String name, String code, String phoneNumber, String idCardNumber) {
        BigDecimal cusId;

        List<CustomerResponse> customerResponseList = new ArrayList<>();
        Response<List<CustomerResponse>> response = new Response<>();

        // search by identity number
        if (idCardNumber != null && phoneNumber == null && (name == null && code == null)) {
            try {
                cusId = cusRepo.findCustomerIdByIDNumber(idCardNumber);
            } catch (Exception e) {
                return null;
            }
        }
        // search by phone number
        else if (phoneNumber != null && idCardNumber == null && (name == null && code == null)) {
            try {
                cusId = BigDecimal.valueOf(cusRepo.findByPhoneNumber(phoneNumber).getId());
            } catch (Exception e) {
                return null;
            }
        }
        // search by name or code
        else if ((name != null || code != null) && idCardNumber == null && phoneNumber == null) {
            try {
                List<BigDecimal> listId = cusRepo.findCustomerIdByNameOrCode(name, code);
                for (BigDecimal id : listId) {
                    Response<CustomerResponse> customer = customerService.getById(id.longValue());
                    System.out.println(customer.getData().getPhoneNumber());
                    customerResponseList.add(customer.getData());
                }
                response.setData(customerResponseList);
                return response;
            } catch (Exception e) {
                return null;
            }
        }
        // search by name and phone number
        else if (phoneNumber != null && (name != null || code != null) && idCardNumber == null) {
            try {
                cusId = cusRepo.findCustomerIdByNameOrCodeAndPhoneNumber(name, code, phoneNumber);
            } catch (Exception e) {
                return null;
            }
        }
        // search by name and identity number
        else if ((name != null || code != null) && idCardNumber != null && phoneNumber == null) {
            try {
                cusId = cusRepo.findCustomerIdByIDNumberAndNameOrCode(idCardNumber, name, code);
            } catch (Exception e) {
                return null;
            }
        }
        // search by phone number and identity number
        else if ((name == null && code == null) && idCardNumber != null && phoneNumber != null) {
            try {
                cusId = cusRepo.findCustomerByPhoneNUmberAndIDNumber(phoneNumber, idCardNumber);
            } catch (Exception e) {
                return null;
            }
        }
        // search by all fields
        else {
            try {
                cusId = cusRepo.findCustomerByAllField(name, code, phoneNumber, idCardNumber);
            } catch (Exception e) {
                return null;
            }
        }
        if(cusId != null)
            customerResponseList.add(customerService.getById(cusId.longValue()).getData());
        response.setData(customerResponseList);

        return response;
    }

}
