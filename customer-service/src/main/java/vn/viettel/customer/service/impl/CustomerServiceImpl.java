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
import vn.viettel.core.util.VNCharacterUtils;
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

        //set card type id in table ap_param
        if(request.getCardTypeId() != null)
        {
            ApParam cardType = apParamClient.getApParamById(request.getCardTypeId()).getData();
            if(cardType == null)
                throw new ValidateException(ResponseMessage.CARD_TYPE_NOT_EXISTS);
            customerRecord.setCardTypeId(request.getCardTypeId());
        }

        if(request.getCloselyTypeId() != null)
        {
            ApParam closelyType = apParamClient.getApParamById(request.getCloselyTypeId()).getData();
            if(closelyType == null)
                throw new ValidateException(ResponseMessage.CLOSELY_TYPE_NOT_EXISTS);
            customerRecord.setCloselyTypeId(request.getCloselyTypeId());
        }

        //set create user
        if(userId != null) {
            customerRecord.setCreateUser(userClient.getUserById(userId).getUserAccount());
        }
        customerRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        //check precinct
        Area precinct = areaService.getByIdAndType(request.getAreaId(),3).getData();
        if(precinct == null)
            throw new ValidateException(ResponseMessage.PRECINCT_NOT_EXITS);

        //set full name not accent
        customerRecord.setFirstNameNotAccent(VNCharacterUtils.removeAccent(customerRecord.getFirstName()).toLowerCase(Locale.ROOT));
        customerRecord.setLastNameNotAccent(VNCharacterUtils.removeAccent(customerRecord.getLastName()).toLowerCase(Locale.ROOT));

        Customer customerResult = repository.save(customerRecord);

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
        Optional<Customer> customer = repository.findById(id);
        if(!customer.isPresent())
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        AreaDTO areaDTO = new AreaDTO();
        if(customer.get().getAreaId()!=null)
        {
            Area precinct = areaService.getAreaById(customer.get().getAreaId()).getData();
            if(precinct == null)
                throw new ValidateException(ResponseMessage.PRECINCT_NOT_EXITS);
            Area district = areaService.getAreaById(precinct.getParentAreaId()).getData();
            if(district == null)
                throw new ValidateException(ResponseMessage.DISTRICT_NOT_EXITS);
            Area province = areaService.getAreaById(district.getParentAreaId()).getData();
            if(province == null)
                throw new ValidateException(ResponseMessage.PROVINCE_NOT_EXITS);
            areaDTO.setPrecinctId(precinct.getId());
            areaDTO.setDistrictId(district.getId());
            areaDTO.setProvinceId(province.getId());
        }else{
            throw new ValidateException(ResponseMessage.AREA_NOT_EXISTS);
        }
        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer.get());
        customerDTO.setAreaDTO(areaDTO);

        return response.withData(customerDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> update(CustomerRequest request, Long userId) {

        Optional<Customer> customerOld = repository.findById(request.getId());
        if (!customerOld.isPresent()) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Customer customerRecord = modelMapper.map(request, Customer.class);
        customerRecord.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        if(userId!=null) {
            customerRecord.setUpdateUser(userClient.getUserById(userId).getUserAccount());
        }

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