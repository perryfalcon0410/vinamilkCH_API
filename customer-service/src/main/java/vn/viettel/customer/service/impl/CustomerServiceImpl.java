package vn.viettel.customer.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.dto.common.AreaDetailDTO;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.dto.customer.*;
import vn.viettel.core.messaging.CustomerOnlRequest;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.util.AgeCalculator;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.VNCharacterUtils;
import vn.viettel.customer.entities.Customer;
import vn.viettel.customer.entities.CustomerType;
import vn.viettel.customer.entities.Customer_;
import vn.viettel.customer.entities.MemberCustomer;
import vn.viettel.customer.messaging.CusRedInvoiceFilter;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.customer.messaging.CustomerSaleFilter;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.repository.MemBerCustomerRepository;
import vn.viettel.customer.repository.RptCusMemAmountRepository;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.CustomerTypeService;
import vn.viettel.customer.service.MemberCardService;
import vn.viettel.customer.service.MemberCustomerService;
import vn.viettel.customer.service.dto.ExportCustomerDTO;
import vn.viettel.customer.service.feign.*;
import vn.viettel.customer.specification.CustomerSpecification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    CustomerTypeRepository customerTypeRepository;

    @Autowired
    RptCusMemAmountRepository rptCusMemAmountRepository;

    @Autowired
    CustomerTypeService customerTypeService;

    @Autowired
    MemBerCustomerRepository memBerCustomerRepos;

    private List<CustomerDTO> customers;

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer, List<MemberCustomer> memberCustomers) {
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

        if(memberCustomers != null) {
            for (MemberCustomer memberCustomer : memberCustomers) {
                if (memberCustomer.getCustomerId().equals(customer.getId())) {
                    dto.setAmountCumulated(memberCustomer.getScoreCumulated());
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
                        .and(CustomerSpecification.hasPhoneToCustomer(filter.getPhone()))
                        .and(CustomerSpecification.hasIdNo(filter.getIdNo()))), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<CategoryDataDTO> genders =  categoryDataClient.getGendersV1().getData();
        Page<CustomerDTO> response = customers.map(item -> {
            CustomerDTO dto = modelMapper.map(item, CustomerDTO.class);
            for (CategoryDataDTO gender : genders) {
                if (gender.getId().equals(item.getGenderId())) {
                    dto.setGenderName(gender.getCategoryName());
                    break;
                }
            }

            return  dto;
        });
       return response;
    }

    @Override
    public Page<CustomerDTO> findCustomerForSale(Long shopId, CustomerSaleFilter customerFilter, Pageable pageable) {
        if(customerFilter == null || customerFilter.getSearchKeywords() == null || customerFilter.getSearchKeywords().isEmpty())
            return  new PageImpl<>(new ArrayList<>());

        Long shop = null;
        Page<CustomerDTO> response = null;
        if(customerFilter.isCustomerOfShop()) shop = shopId;
        if(customerFilter.isSearchPhoneOnly())
            response =  repository.searchForSaleFone(shop, customerFilter.getSearchKeywords(), pageable);
        else {
            response = repository.searchForSale(shop, customerFilter.getSearchKeywords().toUpperCase(), customerFilter.getSearchKeywords(), pageable);
        }
        return response;
    }

    @Override
    public Page<CustomerDTO> findCustomerForRedInvoice(CusRedInvoiceFilter filter, Pageable pageable) {
        if(filter.getSearchKeywords() != null) filter.setSearchKeywords(filter.getSearchKeywords().toUpperCase());
        Page<CustomerDTO> response = repository.searchForRedInvoice(
                filter.getSearchKeywords(), filter.getMobiphone(), filter.getWorkingOffice(), filter.getOfficeAddress(), filter.getTaxCode(), pageable);
        return response;
    }

    @Override
    public Double getScoreCumulated(Long customerId) {
        if(customerId == null)
            return null;

        Optional<MemberCustomer> memberCustomer = memBerCustomerRepos.findByCustomerId(customerId);
        if(memberCustomer.isPresent()) return memberCustomer.get().getScoreCumulated();

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO create(CustomerRequest request, Long userId, Long shopId) {

        //checkphone
        List<Customer> checkPhone = repository.getAllByMobiPhoneAndStatus(request.getMobiPhone(), 1);
        if (checkPhone.size() > 0) {
            String customerInfo = "";
            for(int i=0; i< checkPhone.size(); i++){
                Customer customer = checkPhone.get(i);
                customerInfo +=customer.getCustomerCode()+"-"+customer.getLastName()+" "+customer.getFirstName();
                if(i < checkPhone.size()-1){
                    customerInfo +=", ";
                }
            }
            throw new ValidateException(ResponseMessage.CUSTOMERS_EXIST_FONE,customerInfo );
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
        ApParamDTO ap = apParamClient.getApParamByCodeV1("MIN_AGE").getData();
        String ageApparam = "0";
        if(ap != null && ap.getStatus() == 1) ageApparam = ap.getApParamName();
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


        if(request.getWorkingOffice()!=null && !request.getWorkingOffice().isEmpty()) {
            customerRecord.setWorkingOfficeText(VNCharacterUtils.removeAccent(request.getWorkingOffice().trim()).toUpperCase());
        }

        if(request.getOfficeAddress()!=null && !request.getOfficeAddress().isEmpty()) {
            customerRecord.setOfficeAddressText(VNCharacterUtils.removeAccent(request.getOfficeAddress().trim()).toUpperCase());
        }

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

        //Lưu ko có time -> đầu ngày
        if(request.getIdNoIssuedDate()!=null) customerRecord.setIdNoIssuedDate(DateUtils.convertFromDate(request.getIdNoIssuedDate()));

        customerRecord.setShopId(shopId);
        customerRecord.setCustomerCode(this.createCustomerCode(shopId, shop.getShopCode()));
        Customer customerResult = repository.save(customerRecord);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customerResult, null);
        return customerDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO createForOnlOrder(CustomerOnlRequest request, Long shopId) {
        ShopDTO shop = shopClient.getShopByIdV1(shopId).getData();
        if (shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        CustomerTypeDTO customerType = customerTypeService.getCustomerTypeDefaut();

        Customer customer = new Customer();
        customer.setCustomerCode(this.createCustomerCode(shopId, shop.getShopCode()));
        customer.setShopId(shopId);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setNameText(VNCharacterUtils.removeAccent(request.getLastName()+" "+request.getFirstName()).toUpperCase(Locale.ROOT));
        customer.setMobiPhone(request.getMobiPhone());
        customer.setDob(request.getDob());
        customer.setCustomerTypeId(customerType.getId());
        customer.setAddress(request.getAddress());
        if(request.getAddress()!=null) customer.setAddressText(VNCharacterUtils.removeAccent(request.getAddress()).toUpperCase(Locale.ROOT));
        customer.setStatus(request.getStatus());
        repository.save(customer);

       return this.mapCustomerToCustomerResponse(customer, null);
    }

    public String createCustomerCode(Long shopId, String shopCode) {
        Page<Customer> customers = repository.getLastCustomerNumber(shopId, PageRequest.of(0,1));
        int STT = 1;
        if(!customers.getContent().isEmpty()) {
            String str = customers.getContent().get(0).getCustomerCode();
            String numberString = str.substring(str.length() - 5);
            STT = Integer.valueOf(numberString) + 1;
        }

        return "CUS." + shopCode + "." + Integer.toString(STT + 100000).substring(1);
    }

    public void setAddressAndAreaId(String street, Long areaId, Customer customer){
        String address = "";
        if(areaId != null) {
            AreaDTO areaDTO = areaClient.getByIdV1(areaId).getData();
            if (street != null) {
                address += street + ", ";
                customer.setStreet(street);
            }
            address += areaDTO.getAreaName()+", ";
            address += areaDTO.getDistrictName()+", ";
            address += areaDTO.getProvinceName();

            customer.setAddress(address);
            customer.setAddressText(VNCharacterUtils.removeAccent(address.toUpperCase()));
            customer.setAreaId(areaId);
        }
    }

    @Override
    public CustomerDTO getCustomerById(Long id,Long shopId) {
        Customer customer = repository.getById(id);
        if(customer == null)
                throw new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST);

        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customer, null);

        MemberCustomer memberCustomer = memBerCustomerRepos.getMemberCustomer(customer.getId()).orElse(null);
        if(memberCustomer != null){
            customerDTO.setAmountCumulated(memberCustomer.getScoreCumulated());
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
        //level edit customer
        Long level;
        level = shopClient.getLevelUpdateCustomerV1(shopId).getData();
        if(!customer.getShopId().equals(shopId)){
            if(level!= null){
                customerDTO.setIsEdit(level);
            }else  {
                customerDTO.setIsEdit(0L);
            }
        }else customerDTO.setIsEdit(3L);
        //edit customer type
        CustomerType cusType = customerTypeRepository.getById(customer.getCustomerTypeId());
        if(cusType != null) {
            if(cusType.getPosModifyCustomer()==1) customerDTO.setIsEditCusType(1L);
            CustomerTypeDTO customerType =  modelMapper.map(cusType, CustomerTypeDTO.class);
            customerDTO.setCustomerType(customerType);
        }

        //list top five product
//        List<String> lstProduct = saleOrderClient.getTopFiveFavoriteProductsV1(customer.getId()).getData();
//        customerDTO.setLstProduct(lstProduct);

        return customerDTO;
    }

    @Override
    public List<CustomerDTO> getCustomerByMobiPhone(String phone) {
        List<Customer> customers = repository.getAllByMobiPhoneAndStatus(phone, 1);
        List<CustomerDTO> customerDTOS = customers.stream().map(c -> {
            CustomerDTO customerDTO =  modelMapper.map(c, CustomerDTO.class);
            MemberCustomer memberCustomer = memBerCustomerRepos.getMemberCustomer(c.getId()).orElse(null);
            if(memberCustomer != null) customerDTO.setAmountCumulated(memberCustomer.getScoreCumulated());
            return customerDTO;
        }).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO update(CustomerRequest request, Long userId, Long shopId, Boolean checkUpdate) {
        Customer customerDb= repository.findById(request.getId()).orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST));
        //check level edit
        Long level = shopClient.getLevelUpdateCustomerV1(shopId).getData();
        if(!customerDb.getShopId().equals(shopId)){
            if(level == 0L && checkUpdate){
                throw new ValidateException(ResponseMessage.CUSTOMER_CAN_NOT_UPDATE);
            }
        }

        //checkphone
        if (!request.getMobiPhone().equals(customerDb.getMobiPhone())) {
            List<Customer> checkPhone = repository.getAllByMobiPhoneAndStatus(request.getMobiPhone(), 1);
            if (checkPhone.size() > 0) {
                String customerInfo = "";
                for(int i=0; i< checkPhone.size(); i++){
                    Customer customer = checkPhone.get(i);
                    customerInfo +=customer.getCustomerCode()+"-"+customer.getLastName()+" "+customer.getFirstName();
                    if(i < checkPhone.size()-1){
                        customerInfo +=", ";
                    }
                }
                throw new ValidateException(ResponseMessage.CUSTOMERS_EXIST_FONE,customerInfo );
            }
        }

        if(request.getIdNo()!=null)
        {
            if (!request.getIdNo().equals(customerDb.getIdNo())) {
                Optional<Customer> checkIdNo = repository.getCustomerByIdNo(request.getIdNo());
                if (checkIdNo.isPresent()) {
                    Customer customer = checkIdNo.get();
                    throw new ValidateException(ResponseMessage.CUSTOMERS_EXIST_IDNO, customer.getCustomerCode()+"-"+customer.getLastName()+" "+customer.getFirstName());
                }
            }
        }

        //check age
        int age = AgeCalculator.calculateAge(request.getDob().toLocalDate(), LocalDate.now());
        ApParamDTO ap = apParamClient.getApParamByCodeV1("MIN_AGE").getData();
        String ageApparam = "0";
        if(ap != null && ap.getStatus() == 1) ageApparam = ap.getApParamName();
        if(age < Integer.parseInt(ageApparam)){
            throw new ValidateException(ResponseMessage.CUSTOMER_AGE_NOT_BE_YOUNGER, ageApparam);
        }

        Customer newCustomer = this.mapCustomerUpdate(customerDb, request);

        if(request.getWorkingOffice()!=null && !request.getWorkingOffice().isEmpty()) {
            newCustomer.setWorkingOfficeText(VNCharacterUtils.removeAccent(request.getWorkingOffice().trim()).toUpperCase());
        }
        if(request.getOfficeAddress()!=null && !request.getOfficeAddress().isEmpty()) {
            newCustomer.setOfficeAddressText(VNCharacterUtils.removeAccent(request.getOfficeAddress().trim()).toUpperCase());
        }

        repository.save(newCustomer);

        return this.mapCustomerToCustomerResponse(newCustomer, null);
    }

    private Customer mapCustomerUpdate(Customer customer, CustomerRequest request) {
        if(request.getIdNo()!=null && request.getIdNo().isEmpty()) request.setIdNo(null);

        if(request.getFirstName()!=null) customer.setFirstName(request.getFirstName());
        if(request.getLastName()!=null) customer.setLastName(request.getLastName());

        String fullName = request.getLastName()+" "+request.getFirstName();
        customer.setNameText(VNCharacterUtils.removeAccent(fullName).toUpperCase(Locale.ROOT));

        if(request.getGenderId()!=null) customer.setGenderId(request.getGenderId());
        if(request.getBarCode()!=null) customer.setBarCode(request.getBarCode());
        if(request.getDob()!=null) customer.setDob(request.getDob());
        if(request.getCustomerTypeId()!=null) customer.setCustomerTypeId(request.getCustomerTypeId());
        if(request.getStatus()!=null) customer.setStatus(request.getStatus());
        if(request.getIsPrivate()!=null) customer.setIsPrivate(request.getIsPrivate());

        if(request.getIdNo()!=null){
            customer.setIdNo(request.getIdNo());
        }else {
            customer.setIdNoIssuedDate(null);
            customer.setIdNoIssuedPlace(null);
        }
        if(request.getIdNo()!=null && request.getIdNoIssuedDate()!=null) customer.setIdNoIssuedDate(DateUtils.convertFromDate(request.getIdNoIssuedDate()));//đầu ngày
        if(request.getIdNo()!=null && request.getIdNoIssuedPlace()!=null) customer.setIdNoIssuedPlace(request.getIdNoIssuedPlace());

        if(request.getMobiPhone()!=null) customer.setMobiPhone(request.getMobiPhone());
        if(request.getEmail()!=null) customer.setEmail(request.getEmail());
        //setAddress();setAreaId(),setStreet();
        setAddressAndAreaId(request.getStreet(), request.getAreaId(), customer);
        if(request.getWorkingOffice()!=null) customer.setWorkingOffice(request.getWorkingOffice());
        if(request.getOfficeAddress()!=null) customer.setOfficeAddress(request.getOfficeAddress());
        if(request.getTaxCode()!=null) customer.setTaxCode(request.getTaxCode());
        if(request.getCloselyTypeId()!=null) customer.setCloselyTypeId(request.getCloselyTypeId());
        if(request.getCardTypeId()!=null) customer.setCardTypeId(request.getCardTypeId());

        if(request.getNoted()!=null) customer.setNoted(request.getNoted());

        return customer;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO updateForSale(CustomerRequest request, Long shopId) {
        Customer customerDb= repository.findById(request.getId()).orElseThrow(() -> new ValidateException(ResponseMessage.CUSTOMER_DOES_NOT_EXIST));
        if(request.getTotalBill()!=null) customerDb.setTotalBill(request.getTotalBill());
        if(request.getDayOrderAmount()!=null) customerDb.setDayOrderAmount(request.getDayOrderAmount());
        if(request.getDayOrderNumber()!=null) customerDb.setDayOrderNumber(request.getDayOrderNumber());
        if(request.getMonthOrderAmount()!=null) customerDb.setMonthOrderAmount(request.getMonthOrderAmount());
        if(request.getMonthOrderNumber()!=null) customerDb.setMonthOrderNumber(request.getMonthOrderNumber());
        if(request.getWorkingOffice()!=null) customerDb.setWorkingOffice(request.getWorkingOffice());
        if(request.getTaxCode()!=null) customerDb.setTaxCode(request.getTaxCode());
        if(request.getOfficeAddress()!=null) customerDb.setOfficeAddress(request.getOfficeAddress());
        customerDb.setLastOrderDate(LocalDateTime.now());
        repository.save(customerDb);
        return this.mapCustomerToCustomerResponse(customerDb, null);
    }

    @Override
    public ByteArrayInputStream exportExcel(CustomerFilter filter) throws IOException {
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
                        .and(CustomerSpecification.hasPhoneToCustomer(filter.getPhone()))
                        .and(CustomerSpecification.hasIdNo(filter.getIdNo()))
                ),

                Sort.by(Sort.Direction.ASC, "customerCode").and(Sort.by(Sort.Direction.ASC, "mobiPhone")));

        Map<Long, String> customerTypeMaps = new HashMap<>();
        if(!customers.isEmpty()) {
            List<CustomerTypeDTO> customerTypes = customerTypeService.findByIds(customers.stream().map(item -> item.getCustomerTypeId())
                    .distinct().filter(Objects::nonNull).collect(Collectors.toList()));
            for(CustomerTypeDTO type: customerTypes) {
                if(!customerTypeMaps.containsKey(type.getId())) customerTypeMaps.put(type.getId(), type.getName());
            }
        }

        List<ApParamDTO> closelyTypes = apParamClient.getCloselytypesV1().getData();
        Map<Long, String> closelyTypeMaps = new HashMap<>();
        for(ApParamDTO closely: closelyTypes) {
            if(!closelyTypeMaps.containsKey(closely.getId())) closelyTypeMaps.put(closely.getId(), closely.getApParamName());
        }

        List<ApParamDTO> cardTypes = apParamClient.getCardTypesV1().getData();
        Map<Long, String> cardTypeMaps = new HashMap<>();
        for(ApParamDTO cardType: cardTypes) {
            if(!cardTypeMaps.containsKey(cardType.getId())) cardTypeMaps.put(cardType.getId(), cardType.getApParamName());
        }

        List<CategoryDataDTO> genders = categoryDataClient.getGendersV1().getData();
        Map<Long, String> genderMaps = new HashMap<>();
        for(CategoryDataDTO category: genders) {
            if(!genderMaps.containsKey(category.getId())) genderMaps.put(category.getId(), category.getCategoryName());
        }

        CustomerExcelExporter excel = new CustomerExcelExporter(customers, customerTypeMaps, closelyTypeMaps, cardTypeMaps, genderMaps);
        return excel.export();
    }

    /*
        Đổi hàng hỏng  - hàng trả lại
     */
    @Override
    public Page<CustomerDTO> getAllCustomerForChangeProducts(String searchKeywords, Pageable pageable) {
        if(searchKeywords == null || searchKeywords.isEmpty() || searchKeywords.length() < 4) return  new PageImpl<>(new ArrayList<>());
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        if(searchKeywords.length() == 4) {
            customers = repository.searchForAutoComplete(searchKeywords.toUpperCase(), searchKeywords);
        }
        List<CustomerDTO> results = new ArrayList<>();
        if(customers != null){
            for(CustomerDTO cus : customers){
                if((cus.getCustomerCode() != null && cus.getCustomerCode().contains(searchKeywords.toUpperCase()))
                        || (cus.getNameText() != null && cus.getNameText().contains(searchKeywords.toUpperCase()))
                        || (cus.getAddress() != null && cus.getAddress().contains(searchKeywords))
                        || (cus.getPhone() != null && cus.getPhone().contains(searchKeywords))
                        || (cus.getMobiPhone() != null && cus.getMobiPhone().contains(searchKeywords)) ){
                    results.add(cus);
                    if(results.size() == pageable.getPageSize()) break;
                }
            }
        }
        return new PageImpl<>(results);
    }

    /*@Override
    public Page<CustomerDTO> getAllCustomerForChangeProducts(String searchKeywords, Pageable pageable) {
        if(searchKeywords == null || searchKeywords.isEmpty()) return  new PageImpl<>(new ArrayList<>());
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        Page<Customer> customers = repository.findAll( Specification
                .where(CustomerSpecification.haskeySearchForSale(searchKeywords.replaceAll("^\\s+", ""))).and(CustomerSpecification.hasStatus(1)),pageable);
        Page<CustomerDTO> response = customers.map(item -> modelMapper.map(item, CustomerDTO.class));
        return response;
    }*/

    @Override
    public CustomerDTO getCustomerDefault(Long shopId) {
        List<Customer> customers = repository.getCustomerDefault(shopId);
        if(customers.isEmpty()) throw new ValidateException(ResponseMessage.CUSTOMER_DEFAULT_DOES_NOT_EXIST);
        CustomerDTO customerDTO = this.mapCustomerToCustomerResponse(customers.get(0), null);
        MemberCustomer memberCustomer = memBerCustomerRepos.getMemberCustomer(customers.get(0).getId()).orElse(null);
        if(memberCustomer != null){
            customerDTO.setAmountCumulated(memberCustomer.getScoreCumulated());
        }
        return customerDTO;
    }

    @Override
    public CustomerDTO getCustomerDefaultByShop(Long shopId) {
        List<Customer> customers = repository.getCustomerDefault(shopId);
        if(customers.isEmpty()) throw new ValidateException(ResponseMessage.CUSTOMER_DEFAULT_DOES_NOT_EXIST);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerDTO cusDTO = modelMapper.map(customers.get(0), CustomerDTO.class);
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

        List<MemberCustomer> memberCustomers = memBerCustomerRepos.getMemberCustomers(customers.stream().map(i -> i.getId()).collect(Collectors.toList()));
        return customers.stream().map(item -> mapCustomerToCustomerResponse(item, memberCustomers)).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> getAllCustomerToRedInvoice(List<Long> customerIds) {
        List<Customer> customers = repository.getCustomersByIds(customerIds);
        List<CustomerDTO> customerDTOS =  customers.stream().map(customer -> modelMapper.map(customer, CustomerDTO.class)).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerStartDay() {
        repository.schedulerUpdateStartDay();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerStartMonth() {
        repository.schedulerUpdateStartMonth();
    }

}