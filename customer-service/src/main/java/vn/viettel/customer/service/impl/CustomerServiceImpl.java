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
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.dto.customer.MemberCardDTO;
import vn.viettel.core.dto.customer.MemberCustomerDTO;
import vn.viettel.customer.entities.RptCusMemAmount;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.messaging.CustomerRequest;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.RptCusMemAmountRepository;
import vn.viettel.customer.service.*;
import vn.viettel.customer.service.dto.ExportCustomerDTO;
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
    MemberCardService memberCardService;

    @Autowired
    MemberCustomerService memberCustomerService;

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
    RptCusMemAmountRepository rptCusMemAmountRepository;

    @Autowired
    CustomerTypeService customerTypeService;

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

        RptCusMemAmount rptCusMemAmount = rptCusMemAmountRepository.findByCustomerIdAndStatus(dto.getId(), 1).orElse(null);
        if (rptCusMemAmount != null) {
            dto.setScoreCumulated(rptCusMemAmount.getScore());
            dto.setAmountCumulated(rptCusMemAmount.getAmount());
        }

        return dto;
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
        Optional<Customer> checkPhone = repository.getCustomerByPhone(request.getMobiPhone());
        if (checkPhone.isPresent())
            throw new ValidateException(ResponseMessage.PHONE_HAVE_EXISTED);

        //area
        String address = "";
        if (request.getAreaId() != null) {
            AreaDTO precinct = areaService.getAreaById(request.getAreaId()).getData();
            if (!request.getStreet().equals("")) {
                address += request.getStreet() + ", ";
            }
            if (precinct != null && precinct.getType() == 3) {
                address += precinct.getAreaName();
                AreaDTO district = areaService.getAreaById(precinct.getParentAreaId()).getData();
                if (district != null) {
                    address += ", " + district.getAreaName();
                    AreaDTO province = areaService.getAreaById(district.getParentAreaId()).getData();
                    if (province != null) {
                        address += ", " + province.getAreaName();
                    }
                }
                customerRecord.setAddress(address);
                customerRecord.setAreaId(request.getAreaId());
            }
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
        customerRecord.setFirstNameText(VNCharacterUtils.removeAccent(customerRecord.getFirstName()).toUpperCase(Locale.ROOT));
        customerRecord.setLastNameText(VNCharacterUtils.removeAccent(customerRecord.getLastName()).toUpperCase(Locale.ROOT));

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
        Customer customer = repository.findById(id).
                orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST));
        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer);
        if (customer.getAreaId() != null) customerDTO.setAreaDTO(this.getAreaDTO(customer));
        return response.withData(customerDTO);
    }


    private AreaDTO getAreaDTO(Customer customer) {
        AreaDTO areaDTO = new AreaDTO();
        AreaDTO precinct = areaService.getAreaById(customer.getAreaId()).getData();
        if (precinct != null) {
            areaDTO.setPrecinctId(precinct.getId());
            AreaDTO district = areaService.getAreaById(precinct.getParentAreaId()).getData();
            if (district != null) {
                areaDTO.setDistrictId(district.getId());
                AreaDTO province = areaService.getAreaById(district.getParentAreaId()).getData();
                if (province != null)
                    areaDTO.setProvinceId(province.getId());
            }
        }
        return areaDTO;
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

        if (!request.getMobiPhone().equals(customerOld.get().getMobiPhone())) {
            Optional<Customer> checkPhone = repository.getCustomerByPhone(request.getMobiPhone());
            if (checkPhone.isPresent())
                throw new ValidateException(ResponseMessage.PHONE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Customer customerRecord = modelMapper.map(request, Customer.class);
        customerRecord.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        if (userId != null) {
            customerRecord.setUpdateUser(userClient.getUserById(userId).getUserAccount());
        }

        //area
        String address = "";
        if (request.getAreaId() != null) {
            AreaDTO precinct = areaService.getAreaById(request.getAreaId()).getData();
            if (!request.getStreet().equals("")) {
                address += request.getStreet() + ", ";
            }
            if (precinct != null && precinct.getType() == 3) {
                address += precinct.getAreaName();
                AreaDTO district = areaService.getAreaById(precinct.getParentAreaId()).getData();
                if (district != null) {
                    address += ", " + district.getAreaName();
                    AreaDTO province = areaService.getAreaById(district.getParentAreaId()).getData();
                    if (province != null) {
                        address += ", " + province.getAreaName();
                    }
                }
                customerRecord.setAddress(address);
                customerRecord.setAreaId(request.getAreaId());
            }
        }

        //set full name not accent
        customerRecord.setFirstNameText(VNCharacterUtils.removeAccent(customerRecord.getFirstName()).toUpperCase(Locale.ROOT));
        customerRecord.setLastNameText(VNCharacterUtils.removeAccent(customerRecord.getLastName()).toUpperCase(Locale.ROOT));

        customerRecord.setShopId(customerOld.get().getShopId());

        customerRecord = repository.save(customerRecord);

        CustomerDTO customerDTO = modelMapper.map(customerRecord, CustomerDTO.class);

        return new Response<CustomerDTO>().withData(customerDTO);
    }

    @Override
    public Response<Page<CustomerDTO>> find(CustomerFilter filter, Pageable pageable) {
        Response<Page<CustomerDTO>> response = new Response<>();
        String searchKeywords = StringUtils.defaultIfBlank(filter.getSearchKeywords(), StringUtils.EMPTY);

        List<AreaDTO> precincts = null;
        if (filter.getAreaId() != null) {
            precincts = areaService.getPrecinctsByProvinceId(filter.getAreaId()).getData();
        }

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
                ApParam apParam = apParamClient.getApParamById(customer.getCloselyTypeId()).getData();
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

}