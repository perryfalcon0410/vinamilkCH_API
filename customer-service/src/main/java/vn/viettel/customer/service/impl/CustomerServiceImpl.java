package vn.viettel.customer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.enums.customer.Gender;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.*;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.*;
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

//    private UserClient userClient;

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
            customer.setCreateDate(formatDatetime(e.getCreatedAt()));
            customer.setCusCode(e.getCusCode());
            customer.setCusGroup(group.getName());

            customerList.add(customer);
        }
        Response<List<CustomerResponse>> response = new Response<>();
        response.setData(customerList);
        return response;
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
        Response<Customer> response = new Response<>();

        if (cusRequest == null)
            response.setFailure(ResponseMessage.NO_CONTENT);

//        if(checkUserExist(userId) == null)
//            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);

        if (!isCustomerAlreadyExist(cusRequest.getPhoneNumber())) {

            IDCard idCard = createCustomerIdCard(cusRequest.getIdCard());
            MemberCard memberCard = createMemberCard(cusRequest.getCardMember(), userId);
            Company company = createCustomerCompany(cusRequest.getCompany());
            FullAddress address = createAddress(cusRequest.getAddress());

            Customer customer = new Customer();
            setCustomerValue(customer, cusRequest, userId);

            if(checkUserExist(userId) == null)
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
        Response<Customer> response = new Response<>();

        if (cusRequest == null)
            response.setFailure(ResponseMessage.NO_CONTENT);
//        if(checkUserExist(userId) == null)
//            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);

        Customer customer = cusRepo.findById(cusRequest.getId()).get();
        Date date = new Date();
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (customer != null) {

            if(cusRequest.getIdCard() != null) {
                IDCard idCard = idCardRepo.findById(cusRequest.getIdCard().getId()).get();
                if(idCard != null) {
                    idCard = modelMapper.map(cusRequest.getIdCard(), IDCard.class);
                    idCard.setCusId(cusRequest.getId());
                    idCardRepo.save(idCard);
                    customer.setIdCardId(idCard.getId());
                }
            }
            if(cusRequest.getCardMember() != null) {
                MemberCard memberCard = memCardRepo.findById(cusRequest.getCardMember().getId()).get();
                if(memberCard != null) {
                    memberCard = modelMapper.map(cusRequest.getCardMember(), MemberCard.class);
                    memberCard.setCustomerId(cusRequest.getId());
                    memCardRepo.save(memberCard);
                    customer.setCardMemberId(memberCard.getId());
                }
            }
            if(cusRequest.getCompany() != null) {
                Company company = comRepo.findById(cusRequest.getCompany().getId()).get();
                if(company != null) {
                    company = modelMapper.map(cusRequest.getCompany(), Company.class);
                    comRepo.save(company);
                    customer.setCompanyId(company.getId());
                }
            }
            if(cusRequest.getAddress() != null) {
                FullAddress address = addressRepo.findById(cusRequest.getAddress().getId()).get();
                if(address != null) {
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
            return response;
        }

        return null;
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

        if(num < 10) {
            cusNum.append("0000");
        }
        if(num < 100 && num >= 10) {
            cusNum.append("000");
        }
        if(num < 1000 && num >= 100) {
            cusNum.append("00");
        }
        if(num < 10000 && num >= 1000) {
            cusNum.append("0");
        }
        cusNum.append(num);

        return cusNum.toString();
    }

    @Override
    public String createMemberCardCode() {
        return "TODO";
    }

    @Override
    public User checkUserExist(long userId) {
//        User user = userClient.getUserById(userId);
//        return user == null ? null : user;
        return null;
    }

    @Override
    public boolean isCustomerAlreadyExist(String phoneNumber) {
        return cusRepo.findByPhoneNumber(phoneNumber) != null ? true : false;
    }
}