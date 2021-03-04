package vn.viettel.customer.service;

import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.dto.*;

import java.util.List;

public interface CustomerService {
    Response<List<CustomerResponse>> getAll();
    Response<Customer> getById(long id);
    Response<List<Customer>> getByType(int type);
    Response<Customer> createCustomer(CustomerCreateRequest cusRequest, long userId);
    Response<Customer> updateCustomer(CustomerCreateRequest cusRequest, long userId);
    IDCard createCustomerIdCard(IDCardDto idCardDto);
    MemberCard createMemberCard(CardMemberDto memCardDto, long userId);
    Company createCustomerCompany(CompanyDto companyDto);
    FullAddress createAddress(AddressDto addressDto);
    String createCustomerCode();
    boolean isCustomerAlreadyExist(String phoneNumber);
    Response<IDCard> getIDCardById(long id);
    Response<Company> getCompanyById(long id);
    Response<CardMemberResponse>  getMemberCardById(long id);
    Response<String> deleteCustomer(DeleteRequest ids);
}

