package vn.viettel.customer.service;

import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.dto.*;

import java.util.List;

public interface CustomerService {
    Response<List<CustomerResponse>> getAll();
    Response<Customer> createCustomer(CustomerCreateRequest cusRequest, long userId);
    Response<Customer> updateCustomer(CustomerCreateRequest cusRequest, long userId);
    IDCard createCustomerIdCard(IDCardDto idCardDto);
    MemberCard createMemberCard(CardMemberDto memCardDto, long userId);
    Company createCustomerCompany(CompanyDto companyDto);
    FullAddress createAddress(AddressDto addressDto);
    String createCustomerCode();
    String createMemberCardCode();
    User checkUserExist(long userId);
    boolean isCustomerAlreadyExist(String phoneNumber);
}

