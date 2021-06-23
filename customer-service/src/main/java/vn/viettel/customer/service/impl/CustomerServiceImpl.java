package vn.viettel.customer.service.impl;

import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.common.AreaDetailDTO;
import vn.viettel.core.dto.customer.*;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.AgeCalculator;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.exception.ValidateException;
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

import java.time.*;
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

    @Override
    public <D extends BaseDTO> D update(D item, Class<D> clazz) {
        return super.update(item, clazz);
    }

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

    @Autowired
    SaleOrderClient saleOrderClient;

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer, List<RptCusMemAmount> rptCusMemAmounts) {
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

        if(rptCusMemAmounts != null) {
            for (RptCusMemAmount rptCusMemAmount : rptCusMemAmounts) {
                if (rptCusMemAmount.getCustomerId().equals(customer.getId())) {
                    dto.setScoreCumulated(rptCusMemAmount.getScore());
                    dto.setAmountCumulated(rptCusMemAmount.getAmount());
                    break;
                }
            }
        }
        return dto;
    }

    @Override
    public Page<CustomerDTO> index(CustomerFilter filter, Pageable pageable) {

        String searchKeywords = StringUtils.defaultIfBlank(filter.getSearchKeywords(), StringUtils.EMPTY);

        List<AreaDTO> precincts = null;
        if (filter.getAreaId() != null) {
            precincts = areaClient.getPrecinctsByDistrictIdV1(filter.getAreaId()).getData();
        }

        Page<Customer> customers = repository.findAll( Specification
                .where(CustomerSpecification.hasFullNameOrCode(searchKeywords.trim())
                        .and(CustomerSpecification.hasShopId(filter.getShopId(), filter.getIsShop()))
                        .and(CustomerSpecification.hasStatus(filter.getStatus()))
                        .and(CustomerSpecification.hasCustomerTypeId(filter.getCustomerTypeId()))
                        .and(CustomerSpecification.hasGenderId(filter.getGenderId()))
                        .and(CustomerSpecification.hasAreaId(precincts))
                        .and(CustomerSpecification.hasPhone(filter.getPhone()))
                        .and(CustomerSpecification.hasIdNo(filter.getIdNo()))), pageable);

        List<RptCusMemAmount> rptCusMemAmounts = null;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if(customers.getContent().size() != 0)
        {
            rptCusMemAmounts = rptCusMemAmountRepository.findByCustomerIds(customers.getContent().stream().map(i -> i.getId()).collect(Collectors.toList()));
        }
        List<RptCusMemAmount> finalRptCusMemAmounts = rptCusMemAmounts;
        return customers.map(item -> mapCustomerToCustomerResponse(item, finalRptCusMemAmounts));
    }

    @Override
    public Page<CustomerDTO> getAllCustomerToSaleService(String searchKeywords, Pageable pageable) {
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        Page<Customer> customers = repository.findAll( Specification
                .where(CustomerSpecification.hasFullNameOrCodeOrPhone(searchKeywords.trim()))
                        .and(CustomerSpecification.hasStatus(1L)),pageable);

        List<RptCusMemAmount> rptCusMemAmounts = null;
        if(customers.getContent().size() != 0)
        {
            rptCusMemAmounts = rptCusMemAmountRepository.findByCustomerIds(customers.getContent().stream().map(i -> i.getId()).collect(Collectors.toList()));
        }
        List<RptCusMemAmount> finalRptCusMemAmounts = rptCusMemAmounts;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return customers.map(item -> mapCustomerToCustomerResponse(item, finalRptCusMemAmounts));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO create(CustomerRequest request, Long userId, Long shopId) {

        //checkphone
        Customer checkPhone = repository.getCustomerByMobiPhoneAndStatus(request.getMobiPhone(), 1).orElse(null);
        if (checkPhone != null) {
            Customer customer = checkPhone;
            throw new ValidateException(ResponseMessage.CUSTOMERS_EXIST_FONE, customer.getCustomerCode()+"-"+customer.getLastName()+" "+customer.getFirstName());
        }

        if (request.getIdNo() != null) {
            Optional<Customer> checkIdNo = repository.getCustomerByIdNo(request.getIdNo());
            if (checkIdNo.isPresent()) {
                Customer customer = checkIdNo.get();
                throw new ValidateException(ResponseMessage.CUSTOMERS_EXIST_IDNO, customer.getCustomerCode()+"-"+customer.getLastName()+" "+customer.getFirstName());
            }
        }

        //check age
        int age = AgeCalculator.calculateAge(request.getDob().toLocalDate(), LocalDate.now());
        String ageApparam = apParamClient.getApParamByCodeV1("MIN_AGE").getData().getApParamName();
        if(age < Integer.parseInt(ageApparam)){
            throw new ValidateException(ResponseMessage.CUSTOMER_AGE_NOT_BE_YOUNGER, ageApparam);
        }

        if(request.getCustomerTypeId() == 0 || request.getCustomerTypeId() == null)
        {
            CustomerTypeDTO customerType = customerTypeService.getCustomerTypeDefaut();
            request.setCustomerTypeId(customerType.getId());
        }

        ShopDTO shop = shopClient.getShopByIdV1(shopId).getData();
        if (shop == null)
            throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Customer customerRecord = modelMapper.map(request, Customer.class);

        customerRecord.setCustomerCode(this.createCustomerCode(shopId, shop.getShopCode()));

        //address and areaId
        setAddressAndAreaId(request.getStreet(), request.getAreaId(), customerRecord);

        //set card type id in table ap_param
        if (request.getCardTypeId() != null) {
            ApParamDTO cardType = apParamClient.getApParamByIdV1(request.getCardTypeId()).getData();
            if (cardType == null)
                throw new ValidateException(ResponseMessage.CARD_TYPE_NOT_EXISTS);
            customerRecord.setCardTypeId(request.getCardTypeId());
        }

        if (request.getCloselyTypeId() != null) {
            ApParamDTO closelyType = apParamClient.getApParamByIdV1(request.getCloselyTypeId()).getData();
            if (closelyType == null)
                throw new ValidateException(ResponseMessage.CLOSELY_TYPE_NOT_EXISTS);
            customerRecord.setCloselyTypeId(request.getCloselyTypeId());
        }

        //set full name
        String fullName = customerRecord.getLastName()+" "+customerRecord.getFirstName();
        customerRecord.setNameText(VNCharacterUtils.removeAccent(fullName).toUpperCase(Locale.ROOT));

        customerRecord.setShopId(shopId);
        Customer customerResult = repository.save(customerRecord);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customerResult, null);
        return customerDTO;
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
            AreaDTO areaDTO = areaClient.getByIdV1(areaId).getData();
            if (street != null) {
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
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = repository.findById(id).
                orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST));

        Double totalBillMonth = saleOrderClient.getTotalBillForTheMonthByCustomerId(customer.getId(),customer.getLastOrderDate()).getData();
        customer.setMonthOrderAmount(totalBillMonth);
        repository.save(customer);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer, null);
        RptCusMemAmount rptCusMemAmount = rptCusMemAmountRepository.findByCustomerIdAndStatus(customer.getId(), 1).orElse(null);
        if(rptCusMemAmount != null){
            customerDTO.setScoreCumulated(rptCusMemAmount.getScore());
            customerDTO.setAmountCumulated(rptCusMemAmount.getAmount());
        }
        if (customer.getAreaId() != null)
        {
            AreaDTO areaDTO = areaClient.getByIdV1(customer.getAreaId()).getData();
            AreaDetailDTO areaDetailDTO = modelMapper.map(areaDTO, AreaDetailDTO.class);
            if(areaDTO.getParentAreaId()!=null)
            {
                AreaDTO district = areaClient.getByIdV1(areaDTO.getParentAreaId()).getData();
                areaDetailDTO.setProvinceId(district.getParentAreaId());
                areaDetailDTO.setDistrictId(areaDTO.getParentAreaId());
                areaDetailDTO.setPrecinctId(areaDTO.getId());
            }
            customerDTO.setAreaDetailDTO(areaDetailDTO);
        }
        return customerDTO;
    }

    @Override
    public CustomerDTO getCustomerByMobiPhone(String phone) {
        Customer customer = repository.getCustomerByMobiPhoneAndStatus(phone, 1).orElse(null);
        if (customer == null)
            return null;
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO update(CustomerRequest request, Long userId) {

        Optional<Customer> customerOld = repository.findById(request.getId());
        if (!customerOld.isPresent()) {
            throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        }

        if (!request.getMobiPhone().equals(customerOld.get().getMobiPhone())) {
            Customer checkPhone = repository.getCustomerByMobiPhoneAndStatus(request.getMobiPhone(), 1).orElse(null);
            if (checkPhone != null){
                Customer customer = checkPhone;
                throw new ValidateException(ResponseMessage.CUSTOMERS_EXIST_FONE, customer.getCustomerCode()+"-"+customer.getLastName()+" "+customer.getFirstName());
            }
        }

        if(request.getIdNo()!=null)
        {
            if (!request.getIdNo().equals(customerOld.get().getIdNo())) {
                Optional<Customer> checkIdNo = repository.getCustomerByIdNo(request.getIdNo());
                if (checkIdNo.isPresent()) {
                    Customer customer = checkIdNo.get();
                    throw new ValidateException(ResponseMessage.CUSTOMERS_EXIST_IDNO, customer.getCustomerCode()+"-"+customer.getLastName()+" "+customer.getFirstName());
                }
            }
        }

        //check age
        int age = AgeCalculator.calculateAge(request.getDob().toLocalDate(), LocalDate.now());
        String ageApparam = apParamClient.getApParamByCodeV1("MIN_AGE").getData().getApParamName();
        if(age < Integer.parseInt(ageApparam)){
            throw new ValidateException(ResponseMessage.CUSTOMER_AGE_NOT_BE_YOUNGER, ageApparam);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Customer customerRecord = modelMapper.map(request, Customer.class);

        // address and areaId
        setAddressAndAreaId(request.getStreet(), request.getAreaId(), customerRecord);

        //set full name
        String fullName = customerRecord.getLastName()+" "+customerRecord.getFirstName();
        customerRecord.setNameText(VNCharacterUtils.removeAccent(fullName).toUpperCase(Locale.ROOT));

        customerRecord.setShopId(customerOld.get().getShopId());

        Customer customerResult = repository.save(customerRecord);

        return this.mapCustomerToCustomerResponse(customerResult, null);
    }

    @Override
    public List<ExportCustomerDTO> findAllCustomer(CustomerFilter filter) {
        String searchKeywords = StringUtils.defaultIfBlank(filter.getSearchKeywords(), StringUtils.EMPTY);
        List<AreaDTO> precincts = null;
        if (filter.getAreaId() != null) {
            precincts = areaClient.getPrecinctsByDistrictIdV1(filter.getAreaId()).getData();
        }

        List<Customer> customers = repository.findAll( Specification
                .where(CustomerSpecification.hasFullNameOrCode(searchKeywords.trim())
                        .and(CustomerSpecification.hasShopId(filter.getShopId(), filter.getIsShop()))
                        .and(CustomerSpecification.hasStatus(filter.getStatus()))
                        .and(CustomerSpecification.hasCustomerTypeId(filter.getCustomerTypeId()))
                        .and(CustomerSpecification.hasGenderId(filter.getGenderId()))
                        .and(CustomerSpecification.hasAreaId(precincts))
                        .and(CustomerSpecification.hasPhone(filter.getPhone()))
                        .and(CustomerSpecification.hasIdNo(filter.getIdNo()))),
                Sort.by(Sort.Direction.ASC, "customerCode").and(Sort.by(Sort.Direction.ASC, "mobiPhone")));
        List<ExportCustomerDTO> dtos = new ArrayList<>();

        List<CustomerTypeDTO> customerTypes = customerTypeService.findByIds(customers.stream().map(item -> item.getCustomerTypeId())
                .distinct().filter(Objects::nonNull).collect(Collectors.toList()));
        List<CustomerMemberCardDTO> memberCards = memberCardService.getCustomerMemberCard(customers.stream().map(item -> item.getId()).collect(Collectors.toList()));
        List<ApParamDTO> apParams = apParamClient.getCloselytypesV1().getData();

        for (Customer customer : customers) {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            ExportCustomerDTO customerDTO = modelMapper.map(customer, ExportCustomerDTO.class);
            customerDTO.setCustomerTypeName(" ");
            if (customerTypes != null) {
                for(CustomerTypeDTO customerType : customerTypes){
                    if(customerType.getId().equals(customer.getCustomerTypeId())){
                        customerDTO.setCustomerTypeName(customerType.getName());
                        break;
                    }
                }
            }
            customerDTO.setMemberCardName(" ");
            if (memberCards != null) {
                for(CustomerMemberCardDTO memberCard : memberCards){
                    if(memberCard.getCustomerId().equals(customer.getId())){
                        customerDTO.setMemberCardName(memberCard.getMemberCardName());
                        break;
                    }
                }
            }
            customerDTO.setApParamName(" ");
            if (apParams != null) {
                for(ApParamDTO apParam : apParams){
                    if(apParam.getId().equals(customer.getCloselyTypeId())){
                        customerDTO.setApParamName(apParam.getApParamName());
                        break;
                    }
                }
            }
            dtos.add(customerDTO);
        }
        return dtos;
    }

    @Override
    public CustomerDTO getCustomerDefault(Long shopId) {
        Customer customer = customerRepository.getCustomerDefault(shopId)
                .orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_DEFAULT_DOES_NOT_EXIST));
        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer, null);
        RptCusMemAmount rptCusMemAmount = rptCusMemAmountRepository.findByCustomerIdAndStatus(customer.getId(), 1).orElse(null);
        if(rptCusMemAmount != null){
            customerDTO.setScoreCumulated(rptCusMemAmount.getScore());
            customerDTO.setAmountCumulated(rptCusMemAmount.getAmount());
        }
        return customerDTO;
    }

    @Override
    public CustomerDTO getCustomerDefaultByShop(Long shopId) {
        Optional<Customer> cus = repository.getCustomerDefault(shopId);
        if(!cus.isPresent()) throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerDTO cusDTO = modelMapper.map(cus.get(), CustomerDTO.class);
        return  cusDTO;
    }

    @Override
    public List<Long> getIdCustomerBySearchKeyWords(String searchKeywords) {
        String key = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        List<Customer> customers = repository.findAll(Specification.where(CustomerSpecification.hasFullNameOrCodeOrPhone(key.trim())));
        List<Long> ids = customers.stream().map(cus -> cus.getId()).collect(Collectors.toList());
        return ids;
    }

    @Override
    public List<Long> getIdCustomerBy(String searchKeywords, String customerPhone) {
        String keyUpper =  VNCharacterUtils.removeAccent(searchKeywords).toUpperCase(Locale.ROOT);
        List<Long> ids = repository.getCustomerIds( keyUpper , customerPhone);
        return ids;
    }

    @Override
    public List<CustomerDTO> getCustomerInfo(Integer status, List<Long> customerIds){
        if (customerIds == null || customerIds.isEmpty()) return null;
        List<Customer> customers = repository.getCustomerInfo(status, customerIds);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<RptCusMemAmount> rptCusMemAmounts = rptCusMemAmountRepository.findByCustomerIds(customers.stream().map(i -> i.getId()).collect(Collectors.toList()));
        return customers.stream().map(item -> mapCustomerToCustomerResponse(item, rptCusMemAmounts)).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, CustomerDTO> getAllCustomerToRedInvoice() {
        List<Customer> customers = repository.findAll();
        Map<Integer, CustomerDTO> customerDTOS =  new HashMap<>();
        for(Customer customer : customers){
            CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
            Integer id = Math.toIntExact(customerDTO.getId());
            if(id != null)
            customerDTOS.put(id, customerDTO);
        }
        return customerDTOS;
    }
}