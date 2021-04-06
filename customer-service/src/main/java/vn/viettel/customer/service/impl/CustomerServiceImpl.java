package vn.viettel.customer.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.*;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.messaging.*;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.AreaService;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.CustomerTypeService;
import vn.viettel.customer.service.dto.*;
import vn.viettel.customer.service.feign.*;
import vn.viettel.customer.specification.CustomerSpecification;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRepository> implements CustomerService {

    @Autowired
    UserClient userClient;

    @Autowired
    ShopClient shopClient;

    @Autowired
    AreaService areaService;

    @Autowired
    CategoryDataClient categoryDataClient;

    @Autowired
    MemberCardClient memberCardClient;

    @Autowired
    MemberCustomerClient memberCustomerClient;

    @Autowired
    ApParamClient apParamClient;

    @Autowired
    CustomerTypeService customerTypeService;


    @Override
    public Response<Page<CustomerDTO>> index(String searchKeywords, Date fromDate, Date toDate, Long customerTypeId
            , Long status, Long genderId, Long areaId, String phone, String idNo, Pageable pageable) {
        Response<Page<CustomerDTO>> response = new Response<>();
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        if (fromDate == null || toDate == null) {
            LocalDate initial = LocalDate.now();
            fromDate = Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        Page<Customer> customers;
        customers = repository.findAll(Specification
                .where(CustomerSpecification.hasFullNameOrCodeOrPhone(searchKeywords))
                        .and(CustomerSpecification.hasFromDateToDate(fromDate, toDate))
                        .and(CustomerSpecification.hasStatus(status))
                        .and(CustomerSpecification.hasCustomerTypeId(customerTypeId))
                        .and(CustomerSpecification.hasGenderId(genderId))
                        .and(CustomerSpecification.hasAreaId(areaId))
                        .and(CustomerSpecification.hasPhone(phone))
                        .and(CustomerSpecification.hasIdNo(idNo)), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<CustomerDTO> dtos = customers.map(this::mapCustomerToCustomerResponse);
        return response.withData(dtos);
    }

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

        if(customer.getCustomerTypeId()!=null)
        {
            CustomerType customerType = customerTypeService.findById(customer.getCustomerTypeId()).getData();
            if(customerType==null)
            {
                throw new ValidateException(ResponseMessage.CUSTOMER_TYPE_NOT_EXISTS);
            }
            dto.setCustomerType(customerType.getName());
        }
        if(customer.getGenderId()!=null)
        {
            CategoryData gender = categoryDataClient.getCategoryDataById(customer.getGenderId()).getData();
            if(gender==null)
            {
                throw new ValidateException(ResponseMessage.GENDER_NOT_EXISTS);
            }
            dto.setGender(gender.getCategoryName());
        }

        return dto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> create(CustomerRequest request, Long userId) {
        Shop shop = shopClient.getShopById(request.getShopId()).getData();
        if(shop == null)
            throw  new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        if(request.getIdNo()!=null)
        {
            Optional<Customer> checkIdNo = repository.getCustomerByIdNo(request.getIdNo());
            if(checkIdNo.isPresent())
                throw new ValidateException(ResponseMessage.IDENTITY_CARD_CODE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Customer customerRecord = modelMapper.map(request, Customer.class);

        customerRecord.setCustomerCode(this.createCustomerCode(request.getShopId(), shop.getShopCode()));
        //area
        if(request.getAreaId()!=null)
        {
            Area area = areaService.getAreaById(request.getAreaId()).getData();
            if(area == null)
                throw new ValidateException(ResponseMessage.AREA_NOT_EXISTS);
        }

        //member card
        if(request.getMemberCard().getId()!=null)
        {
            Response<MemberCard> memberCard = memberCardClient.getMemberCardById(request.getMemberCard().getId());
            if(memberCard.getData()==null)
            {
                throw  new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
            }
            customerRecord.setMemberCardId(memberCard.getData().getId());
        }

        //set card type id in table ap_param
        if(request.getMemberCard().getCardTypeId() != null)
        {
            customerRecord.setCardTypeId(request.getMemberCard().getCardTypeId());
        }

        if(request.getMemberCard().getCloselyTypeId() != null)
        {
            customerRecord.setCloselyTypeId(request.getMemberCard().getCloselyTypeId());
        }

        //set create user
        if(userId != null) {
            customerRecord.setCreateUser(userClient.getUserById(userId).getUserAccount());
        }
        customerRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        Customer customerResult = repository.save(customerRecord);

        //member customer
        Long idCustomerNew = repository.getCustomerByCustomerCodeAndDeletedAtIsNull(customerRecord.getCustomerCode()).get().getId();
        MemberCustomerDTO memberCustomerDTO = new MemberCustomerDTO();
        memberCustomerDTO.setCustomerId(idCustomerNew);
        memberCustomerDTO.setMemberCardId(customerRecord.getMemberCardId());
        memberCustomerDTO.setIssueDate(request.getMemberCard().getMemberCardIssueDate());
        memberCustomerDTO.setShopId(request.getShopId());
        Response<MemberCustomer> memberCustomer = memberCustomerClient.create(memberCustomerDTO);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customerResult);
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    public String createCustomerCode(Long shopId, String shopCode) {
        int customerNumber = repository.getCustomerNumber(shopId);
        return  "CUS." +  shopCode + "." + Integer.toString(customerNumber + 1 + 100000).substring(1);
    }

    @Override
    public Response<CustomerDTO> getCustomerById(Long id) {
        Response<CustomerDTO> response = new Response<>();
        Customer customer = repository.getCustomerById(id);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer);

        return response.withData(customerDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> update(CustomerRequest request, Long userId) {

        Customer customerOld = repository.getCustomerById(request.getId());
        if (customerOld == null) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        MemberCard memberCard = memberCardClient.update(request.getMemberCard()).getData();

        Customer customerRecord = modelMapper.map(request, Customer.class);
        customerRecord.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        if(userId!=null) {
            customerRecord.setUpdateUser(userClient.getUserById(userId).getUserAccount());
        }
        customerRecord.setMemberCardId(request.getMemberCard().getId());

        customerRecord = repository.save(customerRecord);

        CustomerDTO customerDTO = modelMapper.map(customerRecord, CustomerDTO.class);
        //gender and customer type
        String customerType = customerTypeService.findById(customerRecord.getCustomerTypeId()).getData().getName();
        String gender = categoryDataClient.getCategoryDataById(customerRecord.getGenderId()).getData().getCategoryName();
        customerDTO.setCustomerType(customerType);
        customerDTO.setGender(gender);

        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @Override
    public Response<Page<CustomerDTO>> findAllCustomer(Pageable pageable) {
        Response<Page<CustomerDTO>> response = new Response<>();

        Page<Customer> customers;
        customers = repository.findAll(pageable);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<CustomerDTO> dtos = customers.map(this::mapCustomerToCustomerResponse);

        return response.withData(dtos);
    }

}