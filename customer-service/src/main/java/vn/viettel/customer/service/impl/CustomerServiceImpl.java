package vn.viettel.customer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.enums.customer.CardMemberType;
import vn.viettel.core.db.entity.enums.customer.CustomerType;
import vn.viettel.core.db.entity.enums.customer.Gender;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.*;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.*;
import vn.viettel.customer.service.feign.AddressClient;
import vn.viettel.customer.service.feign.UserClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRepository> implements CustomerService {
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

    @Override
    public Response<List<CustomerResponse>> getAll() {
        List<Customer> customers = cusRepo.findAll();
        List<CustomerResponse> customerList = new ArrayList<>();
        for (Customer e : customers) {
            Group group = groupRepo.findById(e.getGroupId()).get();

            CustomerResponse customer = new CustomerResponse();
            customer.setId(e.getId());
            customer.setCusCode(e.getCusCode());
            customer.setFirstName(e.getFirstName());
            customer.setLastName(e.getLastName());
            customer.setGender(getGender(e.getGender()));
            customer.setPhoneNumber(e.getPhoneNumber());
            customer.setStatus(e.getStatus() == 1 ? "Active" : "InActive");
            if (e.getDOB() != null)
                customer.setDOB(formatDate(e.getDOB()));
            customer.setAddress(getFullAddress(e.getAddressId()));
            customer.setCreateDate(formatDatetime(e.getCreatedAt()));
            customer.setCusCode(e.getCusCode());
            customer.setCusGroup(group.getName());

            customerList.add(customer);
        }
        Response<List<CustomerResponse>> response = new Response<>();
        response.setData(customerList);
        return response;
    }

    @Override
    public Response<Customer> getById(long id) {
        Response<Customer> response = new Response<>();
        try {
            Customer customer = cusRepo.findById(id).get();
            response.setData(customer);
            return response;
        } catch (Exception e) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
    }

    @Override
    public Response<List<Customer>> getByType(int type) {
        Response<List<Customer>> response = new Response<>();
        try {
            List<Customer> customerList = cusRepo.findCustomerByType(type);
            response.setData(customerList);
            return response;
        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
            return response;
        }
    }

    public String getGender(int code) {
        switch (code) {
            case 0:
                return Gender.MALE.getGender();
            case 1:
                return Gender.FEMALE.getGender();
            default:
                return Gender.OTHER.getGender();
        }
    }

    @Override
    public Response<Customer> createCustomer(CustomerCreateRequest cusRequest, long userId) {
        Response<Customer> response = validateRequest(cusRequest, userId);

        if(response.getSuccess() == false)
            return response;

        if (!isCustomerAlreadyExist(cusRequest.getPhoneNumber())) {

            IDCard idCard = createCustomerIdCard(cusRequest.getIdCard());
            MemberCard memberCard = createMemberCard(cusRequest.getCardMember(), userId);
            Company company = createCustomerCompany(cusRequest.getCompany());
            FullAddress address = createAddress(cusRequest.getAddress());

            Customer customer = new Customer();
            setCustomerValue(customer, cusRequest, userId);

            if (checkUserExist(userId) == null)
                response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);

            Date date = new Date();
            LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            customer.setCreatedAt(dateTime);

            if (address != null)
                customer.setAddressId(address.getId());
            if (company != null)
                customer.setCompanyId(company.getId());
            cusRepo.save(customer);

            if (idCard != null) {
                customer.setIdCardId(idCard.getId());
                idCard.setCusId(customer.getId());
            }
            if (memberCard != null) {
                customer.setCardMemberId(memberCard.getId());
                memberCard.setCustomerId(customer.getId());
            }
            customer.setCreatedBy(userId);
            cusRepo.save(customer);

            response.setData(customer);
        } else
            response.setFailure(ResponseMessage.CUSTOMER_PHONE_NUMBER_IS_ALREADY_USED);
        return response;
    }

    @Override
    public Response<Customer> updateCustomer(CustomerCreateRequest cusRequest, long userId) {
        Response<Customer> response = validateRequest(cusRequest, userId);

        if(response.getSuccess() == false)
            return response;

        Customer customer = checkCustomerExist(cusRequest.getId());
        if(customer == null) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        Date date = new Date();
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (customer != null) {

            if (cusRequest.getIdCard() != null) {
                IDCard idCard = idCardRepo.findById(cusRequest.getIdCard().getId()).get();
                if (idCard != null) {
                    idCard = modelMapper.map(cusRequest.getIdCard(), IDCard.class);
                    idCard.setCusId(cusRequest.getId());
                    idCardRepo.save(idCard);
                    customer.setIdCardId(idCard.getId());
                }
            }
            if (cusRequest.getCardMember() != null) {
                MemberCard memberCard = memCardRepo.findById(cusRequest.getCardMember().getId()).get();
                if (memberCard != null) {
                    memberCard = modelMapper.map(cusRequest.getCardMember(), MemberCard.class);
                    memberCard.setCustomerId(cusRequest.getId());
                    memCardRepo.save(memberCard);
                    customer.setCardMemberId(memberCard.getId());
                }
            }
            if (cusRequest.getCompany() != null) {
                Company company = comRepo.findById(cusRequest.getCompany().getId()).get();
                if (company != null) {
                    company = modelMapper.map(cusRequest.getCompany(), Company.class);
                    comRepo.save(company);
                    customer.setCompanyId(company.getId());
                }
            }
            if (cusRequest.getAddress() != null) {
                FullAddress address = addressRepo.findById(cusRequest.getAddress().getId()).get();
                if (address != null) {
                    address = modelMapper.map(cusRequest.getAddress(), FullAddress.class);
                    addressRepo.save(address);
                    customer.setAddressId(address.getId());
                }
            }

            setCustomerValue(customer, cusRequest, userId);
            customer.setUpdatedBy(userId);
            customer.setUpdatedAt(dateTime);
            cusRepo.save(customer);

            response.setData(customer);
        }
        return response;
    }

    public Response<Customer> validateRequest(CustomerCreateRequest request, long userId) {
        Response<Customer> response = new Response<>();

        if (request == null) {
            response.setFailure(ResponseMessage.NO_CONTENT);
            return response;
        }
        if (checkUserExist(userId) == null) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
        return response;
    }

    private Customer setCustomerValue(Customer customer, CustomerCreateRequest cusRequest, long userId) {

        customer.setCusCode(createCustomerCode());
        customer.setFirstName(cusRequest.getFirstName());
        customer.setLastName(cusRequest.getLastName());
        customer.setCusType(cusRequest.getCusType());
        customer.setGender(cusRequest.getGender());
        customer.setPhoneNumber(cusRequest.getPhoneNumber());
        customer.setStatus(cusRequest.getStatus());
        customer.setExclusive(cusRequest.isExclusive());
        customer.setDOB(cusRequest.getDOB());
        customer.setDescription(cusRequest.getDescription());
        customer.setShopId(cusRequest.getShopId());
        customer.setGroupId(cusRequest.getGroupId());

        return customer;
    }

    @Override
    public IDCard createCustomerIdCard(IDCardDto idCardDto) {
        if (idCardDto != null) {
            if (idCardRepo.findByIdNumber(idCardDto.getIdNumber()) == null) {
                IDCard card = new IDCard(idCardDto.getIdNumber(), idCardDto.getIssueDate(), idCardDto.getIssuePlace());
                return idCardRepo.save(card);
            } else {
                // TODO:
            }
        }
        return null;
    }

    @Override
    public MemberCard createMemberCard(CardMemberDto memCardDto, long userId) {
        if (memCardDto != null) {
            Date date = new Date();
            LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            MemberCard card = new MemberCard(memCardDto.getCardType(), memCardDto.getGroupId(), userId);
            card.setCreatedAt(dateTime);
            return memCardRepo.save(card);
        }
        return null;
    }

    @Override
    public Company createCustomerCompany(CompanyDto companyDto) {
        if (companyDto != null) {
            if (comRepo.findByNameAndAddress(companyDto.getName(), companyDto.getAddress()) != null)
                return comRepo.findByNameAndAddress(companyDto.getName(), companyDto.getAddress());
            else {
                Company company = new Company(companyDto.getName(), companyDto.getAddress());
                return comRepo.save(company);
            }
        }
        return null;
    }

    @Override
    public FullAddress createAddress(AddressDto addressDto) {
        if (addressDto != null) {
            // call api create address (address, wardId) then get addressId to pass to FullAddress constructor
            FullAddress address = new FullAddress(addressDto.getCountryId(), addressDto.getAreaId(),
                    addressDto.getProvinceId(), addressDto.getDistrictId(), addressDto.getWardId(), 1);
            return addressRepo.save(address);
        }
        return null;
    }

    @Override
    public String createCustomerCode() {
        int cusNum = cusRepo.getCustomerNumber();
        StringBuilder cusCode = new StringBuilder();
        cusCode.append("CUS.");
        cusCode.append("STORE_CODE"); // waiting for api from shop
        cusCode.append(".");
        cusCode.append(formatCustomerNumber(cusNum));

        return cusCode.toString();
    }

    public String formatCustomerNumber(int number) {
        StringBuilder cusNum = new StringBuilder();
        int num = number + 1;

        if (num < 10) {
            cusNum.append("0000");
        }
        if (num < 100 && num >= 10) {
            cusNum.append("000");
        }
        if (num < 1000 && num >= 100) {
            cusNum.append("00");
        }
        if (num < 10000 && num >= 1000) {
            cusNum.append("0");
        }
        cusNum.append(num);

        return cusNum.toString();
    }

    public User checkUserExist(long userId) {
        try {
            User user = userClient.getUserById(userId);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public Customer checkCustomerExist(long cusId) {
        try {
            Customer customer = cusRepo.findById(cusId).get();
            return customer;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isCustomerAlreadyExist(String phoneNumber) {
        return cusRepo.findByPhoneNumber(phoneNumber) != null ? true : false;
    }

    @Override
    public Response<IDCard> getIDCardById(long id) {
        Response<IDCard> response = new Response<>();
        try {
            response.setData(idCardRepo.findById(id).get());
            return response;
        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
            return response;
        }
    }

    @Override
    public Response<Company> getCompanyById(long id) {
        Response<Company> response = new Response<>();
        try {
            response.setData(comRepo.findById(id).get());
            return response;
        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
            return response;
        }
    }

    @Override
    public Response<CardMemberResponse> getMemberCardById(long id) {
        Response<CardMemberResponse> response = new Response<>();
        try {
            MemberCard memCard = memCardRepo.findById(id).get();
            CardMemberResponse card = new CardMemberResponse();
            card.setId(memCard.getId());
            String type = CardMemberType.getValueOf(memCard.getCardType()).toString();
            card.setCardType(type);
            Customer cus = cusRepo.findById(memCard.getCustomerId()).get();
            card.setCustomerType(CustomerType.getValueOf(cus.getCusType()).toString());
            card.setCreateDate(memCard.getCreatedAt());

            response.setData(card);
            return response;
        } catch (Exception e) {
            response.setFailure(ResponseMessage.DATA_NOT_FOUND);
            return response;
        }
    }

    @Override
    public Response<String> deleteCustomer(DeleteRequest ids) {
        Response<String> response = new Response<>();
        for(Long id: ids.getListId()) {
            try {
                Customer customer = cusRepo.findById(id).get();
                cusRepo.deleteById(id);
                idCardRepo.deleteById(customer.getIdCardId());
                memCardRepo.deleteById(customer.getCardMemberId());
                response.setData(ResponseMessage.SUCCESSFUL.toString());
            } catch (Exception e) {
                response.setFailure(ResponseMessage.DELETE_FAILED);
            }
        }
        return response;
    }

    public String getFullAddress(long id) {
        FullAddress fullAddress = fullAddRepo.findById(id).get();
        if(fullAddress != null) {
            StringBuilder address = new StringBuilder();
            address.append(addressClient.getAddress(fullAddress.getAddressId())).append("/Phuong ");
            address.append(addressClient.getWard(fullAddress.getWardId())).append("/Quan ");
            address.append(addressClient.getDistrict(fullAddress.getDistrictId())).append("/");
            address.append(addressClient.getProvince(fullAddress.getProvinceId())).append("/");
            address.append(addressClient.getCountry(fullAddress.getCountryId()));

            return address.toString();
        }
        return null;
    }
}
