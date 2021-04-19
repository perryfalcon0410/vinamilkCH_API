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
import vn.viettel.core.db.entity.stock.StockCounting;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.messaging.CustomerRequest;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.service.AreaService;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.CustomerTypeService;
import vn.viettel.customer.service.dto.AreaDTO;
import vn.viettel.customer.service.dto.CustomerDTO;
import vn.viettel.customer.service.dto.ExportCustomerDTO;
import vn.viettel.customer.service.feign.*;
import vn.viettel.customer.specification.CustomerSpecification;

import javax.persistence.criteria.CriteriaBuilder;
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
    MemberClient memberClient;

    @Autowired
    ShopClient shopClient;

    @Autowired
    AreaService areaService;

    @Autowired
    CategoryDataClient categoryDataClient;

    @Autowired
    ApParamClient apParamClient;

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerTypeService customerTypeService;

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);
        return dto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> create(CustomerRequest request, Long userId, Long shopId) {
        Shop shop = shopClient.getShopById(shopId).getData();
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        if (request.getIdNo() != null) {
            Optional<Customer> checkIdNo = repository.getCustomerByIdNo(request.getIdNo());
            if (checkIdNo.isPresent())
                throw new ValidateException(ResponseMessage.IDENTITY_CARD_CODE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Customer customerRecord = modelMapper.map(request, Customer.class);

        customerRecord.setCustomerCode(this.createCustomerCode(shopId, shop.getShopCode()));

        //checkphone
        Optional<Customer> checkPhone = repository.getCustomerByPhone(request.getPhone());
        if (checkPhone.isPresent())
            throw new ValidateException(ResponseMessage.PHONE_HAVE_EXISTED);

        //area
        if (request.getAreaId() != null) {
            customerRecord.setAreaId(request.getAreaId());
        }

        //set card type id in table ap_param
        if (request.getCardTypeId() != null) {
            ApParam cardType = apParamClient.getApParamById(request.getCardTypeId()).getData();
            if (cardType == null)
                throw new ValidateException(ResponseMessage.CARD_TYPE_NOT_EXISTS);
            customerRecord.setCardTypeId(request.getCardTypeId());
        }

        if (request.getCloselyTypeId() != null) {
            ApParam closelyType = apParamClient.getApParamById(request.getCloselyTypeId()).getData();
            if (closelyType == null)
                throw new ValidateException(ResponseMessage.CLOSELY_TYPE_NOT_EXISTS);
            customerRecord.setCloselyTypeId(request.getCloselyTypeId());
        }

        //set create user
        if (userId != null) {
            customerRecord.setCreateUser(userClient.getUserById(userId).getUserAccount());
        }
        customerRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        //set full name not accent
        customerRecord.setFirstNameNotAccent(VNCharacterUtils.removeAccent(customerRecord.getFirstName()).toLowerCase(Locale.ROOT));
        customerRecord.setLastNameNotAccent(VNCharacterUtils.removeAccent(customerRecord.getLastName()).toLowerCase(Locale.ROOT));
        customerRecord.setShopId(shopId);


        Customer customerResult = repository.save(customerRecord);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customerResult);
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    public String createCustomerCode(Long shopId, String shopCode) {
        int customerNumber = repository.getCustomerNumber(shopId);
        return "CUS." + shopCode + "." + Integer.toString(customerNumber + 1 + 100000).substring(1);
    }

    @Override
    public Response<CustomerDTO> getCustomerById(Long id) {
        Response<CustomerDTO> response = new Response<>();
        Optional<Customer> customer = repository.findById(id);
        if (!customer.isPresent())
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        AreaDTO areaDTO = new AreaDTO();
        if (customer.get().getAreaId() != null) {
            Area precinct = areaService.getAreaById(customer.get().getAreaId()).getData();
            if (precinct != null) {
                areaDTO.setPrecinctId(precinct.getId());
                Area district = areaService.getAreaById(precinct.getParentAreaId()).getData();
                if (district != null) {
                    areaDTO.setDistrictId(district.getId());
                    Area province = areaService.getAreaById(district.getParentAreaId()).getData();
                    if (province != null)
                        areaDTO.setProvinceId(province.getId());
                }
            }

        }
        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer.get());
        customerDTO.setAreaDTO(areaDTO);

        return response.withData(customerDTO);
    }

    @Override
    public Response<CustomerDTO> getCustomerByPhone(String phone) {
        //Don't need throw error
        CustomerDTO customerDTO = new CustomerDTO();
        Customer customer = repository.findByPhoneOrMobiPhone(phone);
        if (customer != null) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            customerDTO = modelMapper.map(customer, CustomerDTO.class);
        }
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> update(CustomerRequest request, Long userId) {

        Optional<Customer> customerOld = repository.findById(request.getId());
        if (!customerOld.isPresent()) {
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        }

        if (!request.getPhone().equals(customerOld.get().getPhone())) {
            Optional<Customer> checkPhone = repository.getCustomerByPhone(request.getPhone());
            if (checkPhone.isPresent())
                throw new ValidateException(ResponseMessage.PHONE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Customer customerRecord = modelMapper.map(request, Customer.class);
        customerRecord.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        if (userId != null) {
            customerRecord.setUpdateUser(userClient.getUserById(userId).getUserAccount());
        }

        //set full name not accent
        customerRecord.setFirstNameNotAccent(VNCharacterUtils.removeAccent(customerRecord.getFirstName()).toLowerCase(Locale.ROOT));
        customerRecord.setLastNameNotAccent(VNCharacterUtils.removeAccent(customerRecord.getLastName()).toLowerCase(Locale.ROOT));

        customerRecord.setShopId(customerOld.get().getShopId());

        customerRecord = repository.save(customerRecord);

        CustomerDTO customerDTO = modelMapper.map(customerRecord, CustomerDTO.class);

        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @Override
    public Response<Page<CustomerDTO>> find(CustomerFilter filter, Pageable pageable) {
        Response<Page<CustomerDTO>> response = new Response<>();
        String searchKeywords = StringUtils.defaultIfBlank(filter.getSearchKeywords(), StringUtils.EMPTY);

        LocalDate initial = LocalDate.now();
        if (filter.getFromDate() == null)
            filter.setFromDate(Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        if (filter.getToDate() == null)
            filter.setToDate(Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        List<Area> precincts = null;
        if (filter.getAreaId() != null) {
            precincts = areaService.getPrecinctsByProvinceId(filter.getAreaId()).getData();
        }

        List<Area> finalPrecincts = precincts;
        Page<Customer> customers = repository.findAll(Specification
                .where(CustomerSpecification.hasFullNameOrCodeOrPhone(searchKeywords.trim())
                        .and(CustomerSpecification.hasFromDateToDate(filter.getFromDate(), filter.getToDate()))
                        .and(CustomerSpecification.hasStatus(filter.getStatus()))
                        .and(CustomerSpecification.hasCustomerTypeId(filter.getCustomerTypeId()))
                        .and(CustomerSpecification.hasGenderId(filter.getGenderId()))
                        .and(CustomerSpecification.hasAreaId(precincts))
                        .and(CustomerSpecification.hasPhone(filter.getPhone()))
                        .and(CustomerSpecification.hasIdNo(filter.getIdNo()))), pageable);


        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<CustomerDTO> dtos = customers.map(this::mapCustomerToCustomerResponse);
        return response.withData(dtos);
    }

    @Override
    public Response<List<ExportCustomerDTO>> findAllCustomer() {
        Response<List<ExportCustomerDTO>> response = new Response<>();
        List<Customer> customers = repository.findAll();
        List<ExportCustomerDTO> dtos = new ArrayList<>();

        for (Customer customer : customers) {
            ExportCustomerDTO customerDTO = new ExportCustomerDTO();
            customerDTO.setCustomerCode(customer.getCustomerCode());
            customerDTO.setFirstName(customer.getFirstName());
            customerDTO.setLastName(customer.getLastName());
            customerDTO.setGenderId(customer.getGenderId());
            customerDTO.setBarCode(customer.getBarCode());
            customerDTO.setDob(customer.getDob());
            CustomerType customerType = customerTypeService.findById(customer.getCustomerTypeId()).getData();
            if (customerType == null) {
                customerDTO.setCustomerTypeName(" ");
            }else {
                customerDTO.setCustomerTypeName(customerType.getName());
            }

            customerDTO.setStatus(customer.getStatus());
            customerDTO.setIsPrivate(customer.getIsPrivate());
            customerDTO.setIdNo(customer.getIdNo());
            customerDTO.setIdNoIssuedDate(customer.getIdNoIssuedDate());
            customerDTO.setIdNoIssuedPlace(customer.getIdNoIssuedPlace());
            customerDTO.setMobiPhone(customer.getMobiPhone());
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setAddress(customer.getAddress());
            customerDTO.setWorkingOffice(customer.getWorkingOffice());
            customerDTO.setOfficeAddress(customer.getOfficeAddress());
            customerDTO.setTaxCode(customer.getTaxCode());
            customerDTO.setIsDefault(customer.getIsDefault());
            MemberCustomer memberCustomer = memberClient.getMemberCustomerByIdCustomer(customer.getId()).getData();

            if (memberCustomer == null){
                customerDTO.setMemberCardName(" ");
            }else {
                MemberCard memberCard = memberClient.getMemberCardById(memberCustomer.getMemberCardId()).getData();
                customerDTO.setMemberCardName(memberCard.getMemberCardName());
                if (memberCard == null) {
                    throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
                }
            }
            if (customer.getCloselyTypeId() == null){
                customerDTO.setApParamName(" ");
            }else {
                ApParam apParam = apParamClient.getApParamById(customer.getCloselyTypeId()).getData();
                if (apParam == null) {
                    customerDTO.setApParamName(" ");
                }else {
                    customerDTO.setApParamName(apParam.getApParamName());
                }
            }
            customerDTO.setCreatedAt(customer.getCreatedAt());
            customerDTO.setNoted(customer.getNoted());
            dtos.add(customerDTO);
        }
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//         dtos = customers.stream().map(
//                 item -> modelMapper.map(item, ExportCustomerDTO.class)
//         ).collect(Collectors.toList());


        return response.withData(dtos);
    }

    private ExportCustomerDTO mapCustomerToCustomerDTO(Customer customer) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ExportCustomerDTO dto = modelMapper.map(customer, ExportCustomerDTO.class);
        return dto;
    }

    @Override
    public Response<List<Long>> getIdCustomerBySearchKeyWords(String searchKeywords) {
        List<Customer> customers = repository.findAll(Specification.where(CustomerSpecification.hasFullNameOrCodeOrPhone(searchKeywords)));
        List<Long> ids = customers.stream().map(cus -> cus.getId()).collect(Collectors.toList());
        return new Response<List<Long>>().withData(ids);
    }

}