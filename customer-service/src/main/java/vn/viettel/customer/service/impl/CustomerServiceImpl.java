package vn.viettel.customer.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.entities.RptCusMemAmount;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.RptCusMemAmountRepository;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.CustomerTypeService;
import vn.viettel.customer.service.MemberCardService;
import vn.viettel.customer.service.MemberCustomerService;
import vn.viettel.customer.service.dto.ExportCustomerDTO;
import vn.viettel.customer.service.feign.*;
import vn.viettel.customer.specification.CustomerSpecification;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRepository> implements CustomerService {

    @Autowired
    UserClient userClient;

    @Autowired
    MemberCardService memberCardService;

    @Autowired
    MemberCustomerService memberCustomerService;

    @Autowired
    ShopClient shopClient;

    @Autowired
    AreaClient areaClient;

    @Autowired
    CategoryDataClient categoryDataClient;

    @Autowired
    ApParamClient apParamClient;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RptCusMemAmountRepository rptCusMemAmountRepository;

    @Autowired
    CustomerTypeService customerTypeService;

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer) {
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

        RptCusMemAmount rptCusMemAmount = rptCusMemAmountRepository.findByCustomerIdAndStatus(dto.getId(), 1).orElse(null);
        if (rptCusMemAmount != null) {
            dto.setScoreCumulated(rptCusMemAmount.getScore());
            dto.setAmountCumulated(rptCusMemAmount.getAmount());
        }
        return dto;
    }

    @Override
    public Response<Page<CustomerDTO>> index(CustomerFilter filter, Pageable pageable) {
        Response<Page<CustomerDTO>> response = new Response<>();
        String searchKeywords = StringUtils.defaultIfBlank(filter.getSearchKeywords(), StringUtils.EMPTY);

        List<AreaDTO> precincts = null;
        if (filter.getAreaId() != null) {
            precincts = areaClient.getPrecinctsByDistrictId(filter.getAreaId()).getData();
        }

        Page<Customer> customers = repository.findAll( Specification
                .where(CustomerSpecification.hasFullNameOrCodeOrPhone(searchKeywords.trim())
                        .and(CustomerSpecification.hasShopId(filter.getShopId()))
                        .and(CustomerSpecification.hasFromDateToDate(filter.getFromDate(),filter.getToDate()))
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
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> create(CustomerRequest request, Long userId, Long shopId) {
        ShopDTO shop = shopClient.getShopById(shopId).getData();
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
        Optional<Customer> checkPhone = repository.getCustomerByMobiPhone(request.getMobiPhone());
        if (checkPhone.isPresent())
            throw new ValidateException(ResponseMessage.PHONE_HAVE_EXISTED);

        //address and areaId
        setAddressAndAreaId(request.getStreet(), request.getAreaId(), customerRecord);

        //set card type id in table ap_param
        if (request.getCardTypeId() != null) {
            ApParamDTO cardType = apParamClient.getApParamById(request.getCardTypeId()).getData();
            if (cardType == null)
                throw new ValidateException(ResponseMessage.CARD_TYPE_NOT_EXISTS);
            customerRecord.setCardTypeId(request.getCardTypeId());
        }

        if (request.getCloselyTypeId() != null) {
            ApParamDTO closelyType = apParamClient.getApParamById(request.getCloselyTypeId()).getData();
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
        customerRecord.setFirstNameText(VNCharacterUtils.removeAccent(customerRecord.getFirstName()).toUpperCase(Locale.ROOT));
        customerRecord.setLastNameText(VNCharacterUtils.removeAccent(customerRecord.getLastName()).toUpperCase(Locale.ROOT));

        customerRecord.setShopId(shopId);
        Customer customerResult = repository.save(customerRecord);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customerResult);
        return new Response<CustomerDTO>().withData(customerDTO);
    }

    public String createCustomerCode(Long shopId, String shopCode) {
        int customerNumber = 0;
        Customer customer = repository.getCustomerNumber(shopId);
        if(customer != null ) {
            int i = customer.getCustomerCode().lastIndexOf('.');
            String numberString = customer.getCustomerCode().substring(i+1).trim();
            customerNumber = Integer.valueOf(numberString);
        }

        return "CUS." + shopCode + "." + Integer.toString(customerNumber + 1 + 100000).substring(1);
    }

    public void setAddressAndAreaId(String street, Long areaId, Customer customer){
        String address = "";
        if(areaId != null) {
            AreaDTO areaDTO = areaClient.getById(areaId).getData();
            if (!street.equals("")) {
                address += street + ", ";
            }
            address += areaDTO.getAreaName()+", ";
            address += areaDTO.getDistrictName()+", ";
            address += areaDTO.getProvinceName();

            customer.setAddress(address);
            customer.setAreaId(areaId);
        }
    }

    @Override
    public Response<CustomerDTO> getCustomerById(Long id) {
        Response<CustomerDTO> response = new Response<>();
        Customer customer = repository.findById(id).
                orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST));
        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer);
        if (customer.getAreaId() != null)
        {
            AreaDTO areaDTO = areaClient.getById(customer.getAreaId()).getData();
            customerDTO.setAreaDTO(areaDTO);
        }


        return response.withData(customerDTO);
    }

    @Override
    public Response<CustomerDTO> getCustomerByMobiPhone(String phone) {
        Customer customer = repository.getCustomerByMobiPhone(phone).orElse(null);
        if (customer == null)
            return new Response<CustomerDTO>().withData(null);
        return new Response<CustomerDTO>().withData(modelMapper.map(customer, CustomerDTO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> update(CustomerRequest request, Long userId) {

        Optional<Customer> customerOld = repository.findById(request.getId());
        if (!customerOld.isPresent()) {
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        }

        if (!request.getMobiPhone().equals(customerOld.get().getMobiPhone())) {
            Optional<Customer> checkPhone = repository.getCustomerByMobiPhone(request.getMobiPhone());
            if (checkPhone.isPresent())
                throw new ValidateException(ResponseMessage.PHONE_HAVE_EXISTED);
        }

        if (!request.getIdNo().equals(customerOld.get().getIdNo())) {
            Optional<Customer> checkIdNo = repository.getCustomerByIdNo(request.getIdNo());
            if (checkIdNo.isPresent())
                throw new ValidateException(ResponseMessage.IDENTITY_CARD_CODE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Customer customerRecord = modelMapper.map(request, Customer.class);
        customerRecord.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        if (userId != null) {
            customerRecord.setUpdateUser(userClient.getUserById(userId).getUserAccount());
        }

        //address and areaId
        setAddressAndAreaId(request.getStreet(), request.getAreaId(), customerRecord);

        //set full name not accent
        customerRecord.setFirstNameText(VNCharacterUtils.removeAccent(customerRecord.getFirstName()).toUpperCase(Locale.ROOT));
        customerRecord.setLastNameText(VNCharacterUtils.removeAccent(customerRecord.getLastName()).toUpperCase(Locale.ROOT));

        customerRecord.setShopId(customerOld.get().getShopId());

        customerRecord = repository.save(customerRecord);

        CustomerDTO customerDTO = modelMapper.map(customerRecord, CustomerDTO.class);

        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @Override
    public Response<List<ExportCustomerDTO>> findAllCustomer() {
        Response<List<ExportCustomerDTO>> response = new Response<>();
        List<Customer> customers = repository.findAllDesc();
        List<ExportCustomerDTO> dtos = new ArrayList<>();

        for (Customer customer : customers) {
            ExportCustomerDTO customerDTO = new ExportCustomerDTO();
            customerDTO.setCustomerCode(customer.getCustomerCode());
            customerDTO.setFirstName(customer.getFirstName());
            customerDTO.setLastName(customer.getLastName());
            customerDTO.setGenderId(customer.getGenderId());
            customerDTO.setBarCode(customer.getBarCode());
            customerDTO.setDob(customer.getDob());

            if (customer.getCustomerTypeId() == null) {
                customerDTO.setCustomerTypeName(" ");
            } else {
                Response<CustomerTypeDTO> customerType = customerTypeService.findByCustomerTypeId(customer.getCustomerTypeId());
                if (customerType == null) {
                    customerDTO.setCustomerTypeName(" ");
                } else {
                    customerDTO.setCustomerTypeName(customerType.getData().getName());
                }
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
            if (customer.getId() == null) {
                customerDTO.setMemberCardName(" ");
            } else {
                Response<MemberCustomerDTO> memberCustomer = memberCustomerService.getMemberCustomerByIdCustomer(customer.getId());
                if (memberCustomer == null) {
                    customerDTO.setMemberCardName(" ");
                } else {
                    MemberCardDTO memberCard = memberCardService.getMemberCardById(memberCustomer.getData().getMemberCardId()).getData();
                    customerDTO.setMemberCardName(memberCard.getMemberCardName());
                    if (memberCard == null) {
                        throw new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
                    }
                }
            }
            if (customer.getCloselyTypeId() == null) {
                customerDTO.setApParamName(" ");
            } else {
                ApParamDTO apParam = apParamClient.getApParamById(customer.getCloselyTypeId()).getData();
                if (apParam == null) {
                    customerDTO.setApParamName(" ");
                } else {
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

    @Override
    public Response<CustomerDTO> getCustomerDefault(Long shopId) {
        Customer customer = customerRepository.getCustomerDefault(shopId)
                .orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST));
        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer);
        return new Response<CustomerDTO>().withData(customerDTO);
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

    public Response<List<CustomerDTO>> findByNameOrPhone(CustomerFilter filter) {
        Response<List<CustomerDTO>> response = new Response<>();
        String searchKeywords = StringUtils.defaultIfBlank(filter.getSearchKeywords(), StringUtils.EMPTY);
        List<Customer> customers = repository.findAll( Specification
                .where(CustomerSpecification.hasFullNameOrCodeOrPhone(searchKeywords.trim())));
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        for(Customer customer:customers) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            CustomerDTO customerDTO =  modelMapper.map(customer, CustomerDTO.class);
            customerDTOS.add(customerDTO);
        }
        response.setData(customerDTOS);
        return response;
    }
}