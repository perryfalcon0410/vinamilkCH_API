package vn.viettel.customer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.db.entity.enums.customer.CardMemberType;
import vn.viettel.core.db.entity.enums.customer.CustomerType;
import vn.viettel.core.db.entity.enums.customer.Gender;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.interceptor.CheckRoleInterceptor;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.repository.*;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.*;
import vn.viettel.customer.service.feign.AddressClient;
import vn.viettel.customer.service.feign.ShopClient;
import vn.viettel.customer.service.feign.UserClient;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    @Autowired
    ShopClient shopClient;

    private CheckRoleInterceptor checkRoleInterceptor = new CheckRoleInterceptor();

    private Date date = new Date();
    private Timestamp dateTime = new Timestamp(date.getTime());

    @Override
    public Response<Page<CustomerResponse>> getAll(Pageable pageable) {
//        long idUser = checkRoleInterceptor.getUserId();
//        System.out.println("USERID" + idUser);

        Page<Customer> customers = cusRepo.findAll(pageable);
        List<CustomerResponse> customerList = new ArrayList<>();
        for (Customer e : customers) {
            CustomerResponse customer = setCustomerResponse(e);
            customerList.add(customer);
        }
        Page<CustomerResponse> customerResponses = new PageImpl<>(customerList);
        Response<Page<CustomerResponse>> response = new Response<>();
        response.setData(customerResponses);
        return response;
    }

    @Override
    public List<CustomerResponse> findAll() {
        List<Customer> customers = cusRepo.findAll();
        List<CustomerResponse> customerList = new ArrayList<>();

        for (Customer e : customers) {
            CustomerResponse customer = setCustomerResponse(e);
            customerList.add(customer);
        }
        return customerList;
    }

    @Override
    public Response<CustomerResponse> getById(long id) {
        Response<CustomerResponse> response = new Response<>();
        try {
            Customer customer = cusRepo.findById(id).get();
            CustomerResponse customerResponse = setCustomerResponse(customer);
            response.setData(customerResponse);
            return response;
        } catch (Exception e) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }
    }

    @Override
    public Customer findById(long id) {
        Customer customer;
        try {
            customer = cusRepo.findById(id).get();
            System.out.println("ADDRESS: " + customer.getAddressId());
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            customer = null;
        }
        return customer;
    }

    public CustomerResponse setCustomerResponse(Customer customer) {
        Group group;
        IDCard idCard;
        Company company;
        MemberCard memberCard;
        try {
            group = groupRepo.findById(customer.getGroupId()).get();
        } catch (Exception ex) {
            group = null;
        }
        try {
            idCard = idCardRepo.findById(customer.getIdCardId()).get();
        } catch (Exception ex) {
            idCard = null;
        }
        try {
            company = comRepo.findById(customer.getCompanyId()).get();
        } catch (Exception ex) {
            company = null;
        }
        try {
            memberCard = memCardRepo.findById(customer.getCardMemberId()).get();
        } catch (Exception ex) {
            memberCard = null;
        }

        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(customer.getId());
        customerResponse.setCusCode(customer.getCusCode());
        customerResponse.setFirstName(customer.getFirstName());
        customerResponse.setLastName(customer.getLastName());
        customerResponse.setBarCode(customer.getBarCode());
        if (customer.getDOB() != null)
            customerResponse.setDOB(customer.getDOB().toString());
        customerResponse.setGender(getGender(customer.getGender()));
        if (group != null)
            customerResponse.setCusGroup(group.getName());
        customerResponse.setStatus(customer.getStatus() == 1 ? "Active" : "InActive");
        customerResponse.setIsExclusive(customer.isExclusive() ? "True" : "False");
        if (idCard != null) {
            customerResponse.setIdNumber(idCard.getIdNumber());
            if (idCard.getIssueDate() != null)
                customerResponse.setIssueDate(idCard.getIssueDate().toString());
            customerResponse.setIssuePlace(idCard.getIssuePlace());
        }
        customerResponse.setPhoneNumber(customer.getPhoneNumber());
        customerResponse.setEmail(customer.getEmail());
        if (customer.getAddressId() != null)
            customerResponse.setAddress(getFullAddress(customer.getAddressId()));
        if (company != null) {
            customerResponse.setCompany(company.getName());
            customerResponse.setCompanyAddress(company.getAddress());
        }
        customer.setTaxCode(customer.getTaxCode());
        if (memberCard != null) {
            customerResponse.setMemberCardNumber(memberCard.getId().toString());
            if (memberCard.getCreatedAt() != null)
                customerResponse.setMemberCardCreateDate(memberCard.getCreatedAt().toString());
            customerResponse.setMemberCardType(CardMemberType.getValueOf(memberCard.getCardType()).toString());
        }
        customerResponse.setCusType(CustomerType.getValueOf(customer.getCusType()));
        customerResponse.setCusCode(customer.getCusCode());
        if (customer.getCreatedAt() != null)
            customerResponse.setCustomerCreateDate(customer.getCreatedAt().toString());
        customerResponse.setDescription(customer.getDescription());

        return customerResponse;
    }

    @Override
    public Response<List<CustomerResponse>> getByType(int type) {
        Response<List<CustomerResponse>> response = new Response<>();
        try {
            List<Customer> customerList = cusRepo.findCustomerByType(type);
            List<CustomerResponse> responsesList = new ArrayList<>();
            for (Customer customer : customerList) {
                CustomerResponse customerResponse = setCustomerResponse(customer);
                responsesList.add(customerResponse);
            }
            response.setData(responsesList);
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

        if (response.getSuccess() == false)
            return response;

        if (!isCustomerAlreadyExist(cusRequest.getPhoneNumber())) {

            IDCard idCard = createCustomerIdCard(cusRequest.getIdCard());
            MemberCard memberCard = createMemberCard(cusRequest.getCardMember(), userId);
            Company company = createCustomerCompany(cusRequest.getCompany());
            FullAddress address = createAddress(cusRequest.getAddress(), cusRequest.getAddressDetail());

            Customer customer = new Customer();
            setCustomerValue(customer, cusRequest);

            if (checkUserExist(userId) == null) {
                return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
            }
            if (cusRequest.getAddress() != null) {
                if (address != null)
                    customer.setAddressId(address.getId());
                else
                    customer.setAddressId(new Long(0));
            }
            try {
                cusRepo.save(customer);
            } catch (Exception e) {
                System.out.println(e);
                if (idCard != null)
                    idCardRepo.deleteById(idCard.getId());
                if (memberCard != null)
                    memCardRepo.deleteById(memberCard.getId());
                if (address != null)
                    addressRepo.deleteById(address.getId());

                return response.withError(ResponseMessage.CREATE_FAILED);
            }

            if (cusRequest.getCompany() != null) {
                if (company != null)
                    customer.setCompanyId(company.getId());
            }
            if (cusRequest.getIdCard() != null) {
                if (idCard != null) {
                    customer.setIdCardId(idCard.getId());
                    idCard.setCusId(customer.getId());
                    idCardRepo.save(idCard);
                } else {
                    return response.withError(ResponseMessage.ID_CARD_ALREADY_EXIST);
                }
            }
            if (cusRequest.getCardMember() != null) {
                if (memberCard != null) {
                    customer.setCardMemberId(memberCard.getId());
                    memberCard.setCustomerId(customer.getId());
                    memCardRepo.save(memberCard);
                }
            }
            customer.setCreatedAt(dateTime);
            customer.setCreatedBy(userId);

            try {
                cusRepo.save(customer);
                response.setData(customer);
            } catch (Exception e) {
                if (idCard != null)
                    idCardRepo.deleteById(idCard.getId());
                if (memberCard != null)
                    memCardRepo.deleteById(memberCard.getId());
                if (address != null)
                    addressRepo.deleteById(address.getId());
                return response.withError(ResponseMessage.CREATE_FAILED);
            }
        } else
            response.setFailure(ResponseMessage.CUSTOMER_PHONE_NUMBER_IS_ALREADY_USED);
        return response;
    }

    @Override
    public Response<Customer> updateCustomer(CustomerCreateRequest cusRequest, long userId) {
        Response<Customer> response = validateRequest(cusRequest, userId);

        if (response.getSuccess() == false)
            return response;

        // check if customer exist or not
        Customer customer = checkCustomerExist(cusRequest.getId());
        if (customer == null) {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
            return response;
        }

        if (cusRequest.getIdCard() != null) {
            // if customer does not have idCard -> create idCard
            IDCard idCard;
            if (cusRequest.getIdCard().getId() == 0) {
                idCard = createCustomerIdCard(cusRequest.getIdCard());
                if (idCard == null) {
                    return response.withError(ResponseMessage.ID_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE);
                }
                idCard.setCusId(customer.getId());
                customer.setIdCardId(idCard.getId());
                // else -> update idCard
            } else {
                // check is idCard id belong to that customer
                idCard = idCardRepo.findByIdNumber(cusRequest.getIdCard().getIdNumber());
                if (idCard != null) {
                    if (idCard.getCusId() != customer.getId()) {
                        return response.withError(ResponseMessage.ID_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE);
                    }
                    try {
                        idCard = modelMapper.map(cusRequest.getIdCard(), IDCard.class);
                        idCard.setCusId(cusRequest.getId());
                        idCardRepo.save(idCard);
                    } catch (Exception e) {
                        return response.withError(ResponseMessage.ID_CARD_DOES_NOT_EXIST);
                    }
                }
            }
        }

        if (cusRequest.getCardMember() != null) {
            if (cusRequest.getCardMember().getId() == 0) {
                MemberCard memberCard = createMemberCard(cusRequest.getCardMember(), userId);
                memberCard.setCustomerId(customer.getId());
                customer.setCardMemberId(memberCard.getId());
            } else {
                if (cusRequest.getCardMember().getId() != customer.getCardMemberId()) {
                    return response.withError(ResponseMessage.MEMBER_CARD_ALREADY_BELONG_TO_OTHER_PEOPLE);
                }
                try {
                    MemberCard memberCard = modelMapper.map(cusRequest.getCardMember(), MemberCard.class);
                    memberCard.setCustomerId(customer.getId());
                    memCardRepo.save(memberCard);
                } catch (Exception e) {
                    return response.withError(ResponseMessage.MEMBER_CARD_NOT_EXIST);
                }
            }
        }
        if (cusRequest.getCompany() != null) {
            if (cusRequest.getCompany().getId() == 0) {
                Company company = createCustomerCompany(cusRequest.getCompany());
                customer.setCompanyId(company.getId());
            } else {
                if (cusRequest.getCompany().getId() != customer.getCompanyId()) {
                    return response.withError(ResponseMessage.NOT_YOUR_COMPANY);
                }
                try {
                    Company company = modelMapper.map(cusRequest.getCompany(), Company.class);
                    comRepo.save(company);
                } catch (Exception e) {
                    return response.withError(ResponseMessage.COMPANY_DOES_NOT_EXIST);
                }
            }
        }
        if (cusRequest.getAddress() != null) {
            if (cusRequest.getAddress().getId() == 0) {
                FullAddress address = createAddress(cusRequest.getAddress(), cusRequest.getAddressDetail());
                customer.setAddressId(address.getId());
            } else {
                try {
                    FullAddress address = modelMapper.map(cusRequest.getAddress(), FullAddress.class);
                    addressRepo.save(address);
                } catch (Exception e) {
                    return response.withError(ResponseMessage.ADDRESS_DOES_NOT_EXIST);
                }
            }
        }

        setCustomerValue(customer, cusRequest);
        customer.setUpdatedBy(userId);
        customer.setUpdatedAt(dateTime);
        cusRepo.save(customer);

        response.setData(customer);

        return response;
    }

    public Response<Customer> validateRequest(CustomerCreateRequest request, long userId) {
        Response<Customer> response = new Response<>();

        if (request == null) {
            return response.withError(ResponseMessage.NO_CONTENT);
        }
        if (checkUserExist(userId) == null) {
            return response.withError(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        return response;
    }

    public Date parse(String str, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    private Customer setCustomerValue(Customer customer, CustomerCreateRequest cusRequest) {

        try {
            Date birthDay = parse(cusRequest.getDOB(), "dd/MM/yyyy");
            System.out.println("BOD: " + birthDay);
            customer.setDOB(birthDay);
        } catch (Exception e) {
            customer.setDOB(null);
        }

        customer.setCusCode(createCustomerCode(cusRequest.getShopId()));
        customer.setFirstName(cusRequest.getFirstName());
        customer.setLastName(cusRequest.getLastName());
        customer.setCusType(cusRequest.getCusType());
        customer.setGender(cusRequest.getGender());
        customer.setEmail(cusRequest.getEmail());
        customer.setPhoneNumber(cusRequest.getPhoneNumber());
        customer.setStatus(cusRequest.getStatus());
        customer.setExclusive(cusRequest.isExclusive());
        customer.setDescription(cusRequest.getDescription());
        customer.setShopId(cusRequest.getShopId());
        customer.setGroupId(cusRequest.getGroupId());

        return customer;
    }

    @Override
    public IDCard createCustomerIdCard(IDCardDto idCardDto) {
        if (idCardDto != null) {
            if (idCardRepo.findByIdNumber(idCardDto.getIdNumber()) != null)
                return null;
            else {
                IDCard card = new IDCard(idCardDto.getIdNumber(), idCardDto.getIssueDate(), idCardDto.getIssuePlace());
                card.setCreatedAt(dateTime);
                return idCardRepo.save(card);
            }
        }
        return null;
    }

    @Override
    public MemberCard createMemberCard(CardMemberDto memCardDto, long userId) {
        if (memCardDto != null) {
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
    public FullAddress createAddress(AddressDto addressDto, String adrName) {
        if (addressDto != null) {
            // call api create address (address, wardId) then get addressId to pass to FullAddress constructor
            try {
                Address response = addressClient.createAddress(new CreateAddressDto(adrName, addressDto.getWardId()));
                if (response != null) {
                    addressDto.setAddressId(response.getId());
                } else
                    addressDto.setAddressId(0);
                FullAddress address = new FullAddress(addressDto.getCountryId(), addressDto.getAreaId(),
                        addressDto.getProvinceId(), addressDto.getDistrictId(), addressDto.getWardId(), addressDto.getAddressId());
                return addressRepo.save(address);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return null;
    }

    @Override
    public String createCustomerCode(long shopId) {
        int cusNum = cusRepo.getCustomerNumber();
        Shop shop = null;

        StringBuilder cusCode = new StringBuilder();
        cusCode.append("CUS.");
        if (validateShop(shopId) != null) {
            shop = validateShop(shopId);
            cusCode.append(shop.getShopCode());
        }
        cusCode.append(".");
        cusCode.append(formatCustomerNumber(cusNum));

        return cusCode.toString();
    }

    public Shop validateShop(long id) {
        Response<Shop> response = shopClient.getShopById(id);
        if (response.getSuccess() != true)
            return null;
        else
            return response.getData();
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
            return response.withData(idCardRepo.findById(id).get());
        } catch (Exception e) {
            return response.withError(ResponseMessage.DATA_NOT_FOUND);
        }
    }

    @Override
    public Response<Company> getCompanyById(long id) {
        Response<Company> response = new Response<>();
        try {
            return response.withData(comRepo.findById(id).get());
        } catch (Exception e) {
            return response.withError(ResponseMessage.DATA_NOT_FOUND);
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
            card.setCustomerType(CustomerType.getValueOf(cus.getCusType()));
            card.setCreateDate(memCard.getCreatedAt());

            return response.withData(card);
        } catch (Exception e) {
            return response.withError(ResponseMessage.DATA_NOT_FOUND);
        }
    }

    @Override
    public Response<String> deleteCustomer(DeleteRequest ids) {
        Response<String> response = new Response<>();
        for (Long id : ids.getListId()) {
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

    public String getFullAddress(Long id) {
        FullAddress fullAddress;
        try {
            fullAddress = fullAddRepo.findById(id).get();
        } catch (Exception e) {
            return "null";
        }
        StringBuilder address = new StringBuilder();
        if (fullAddress.getAddressId() != null)
            address.append(addressClient.getAddress(fullAddress.getAddressId()));
        address.append("/Phuong ");
        address.append(addressClient.getWard(fullAddress.getWardId())).append("/Quan ");
        address.append(addressClient.getDistrict(fullAddress.getDistrictId())).append("/");
        address.append(addressClient.getProvince(fullAddress.getProvinceId())).append("/");
        address.append(addressClient.getCountry(fullAddress.getCountryId()));

        return address.toString();
    }

}
