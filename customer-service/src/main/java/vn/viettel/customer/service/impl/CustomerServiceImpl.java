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
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.voucher.MemberCard;
import vn.viettel.core.db.entity.voucher.MemberCustomer;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.messaging.*;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.CustomerService;
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
    CustomerTypeRepository customerTypeRepository;

    @Autowired
    UserClient userClient;

    @Autowired
    ShopClient shopClient;

    @Autowired
    CategoryDataClient categoryDataClient;

    @Autowired
    MemberCardClient memberCardClient;

    @Autowired
    MemberCustomerClient memberCustomerClient;

    @Autowired
    ApParamClient apParamClient;

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

        customers = repository.findAll(Specification.where(CustomerSpecification.hasFullNameOrCodeOrPhone(searchKeywords))
                .and(CustomerSpecification.hasFromDateToDate(fromDate, toDate).and(CustomerSpecification.hasStatus(status))
                .and(CustomerSpecification.hasCustomerTypeId(customerTypeId)).and(CustomerSpecification.hasGenderId(genderId)))
                .and(CustomerSpecification.hasAreaId(areaId)).and(CustomerSpecification.hasPhone(phone))
                .and(CustomerSpecification.hasIdNo(idNo)).and(CustomerSpecification.hasDeletedAtIsNull()), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<CustomerDTO> dtos = customers.map(this::mapCustomerToCustomerResponse);
        return response.withData(dtos);
    }

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);

        String customerType = customerTypeRepository.findById(customer.getCustomerTypeId()).get().getName();
        String gender = categoryDataClient.getCategoryDataById(customer.getGenderId()).getCategoryName();
        dto.setCustomerType(customerType);
        dto.setGender(gender);
        return dto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Customer> create(CustomerRequest request, Long userId) {
        Shop shop = shopClient.getShopById(request.getShopId()).getData();
        if(shop == null)
            throw  new ValidateException(ResponseMessage.SHOP_NOT_FOUND);

        if(request.getIdNo()!=null)
        {
            Optional<Customer> checkIdNo = repository.getCustomerByIdNo(request.getIdNo());
            if(checkIdNo.isPresent())
                throw new ValidateException(ResponseMessage.IDENTITY_CARD_CODE_HAVE_EXISTED);
        }

        Optional<Customer> checkphone = repository.getCustomerByPhone(request.getPhone());
        if(checkphone.isPresent())
            throw  new ValidateException(ResponseMessage.PHONE_HAVE_EXISTED);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Customer customerRecord = modelMapper.map(request, Customer.class);

        customerRecord.setCustomerCode(this.createCustomerCode(request.getShopId(), shop.getShopCode()));

        //member card
        if(request.getMemberCard().getMemberCardId()!=null)
        {
            Optional<MemberCard> memberCard = memberCardClient.getMemberCardById(request.getMemberCard().getMemberCardId());
            if(!memberCard.isPresent())
            {
                throw  new ValidateException(ResponseMessage.MEMBER_CARD_NOT_EXIST);
            }
            customerRecord.setMemberCardId(memberCard.get().getId());
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
        repository.save(customerRecord);

        //member customer
        Long idCustomerNew = repository.getCustomerByCustomerCodeAndDeletedAtIsNull(customerRecord.getCustomerCode()).get().getId();
        MemberCustomerDTO memberCustomerDTO = new MemberCustomerDTO();
        memberCustomerDTO.setCustomerId(idCustomerNew);
        memberCustomerDTO.setMemberCardId(customerRecord.getMemberCardId());
        memberCustomerDTO.setIssueDate(request.getMemberCard().getMemberCardIssueDate());
        memberCustomerDTO.setShopId(request.getShopId());
        Response<MemberCustomer> memberCustomer = memberCustomerClient.create(memberCustomerDTO);

        return new Response<Customer>().withData(customerRecord);
    }

    public String createCustomerCode(Long shopId, String shopCode) {
        int customerNumber = repository.getCustomerNumber(shopId);
        return  "CUS." +  shopCode + "." + Integer.toString(customerNumber + 1 + 100000).substring(1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> edit(Long id) {
        Response<CustomerDTO> response = new Response<>();

        Customer customer = repository.getCustomerByIdAndDeletedAtIsNull(id);

        if (!customer.getId().equals(id)) {
            return response.withError(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        //set member card
        MemberCardDTO memberCardDTO = modelMapper
                .map(memberCardClient.getMemberCardById(customer.getMemberCardId()).get(),MemberCardDTO.class);
        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
        Optional<MemberCustomer> memberCustomer = memberCustomerClient.getMemberCustomerByCustomerId(id);
        memberCardDTO.setMemberCardIssueDate(memberCustomer.get().getIssueDate());

        //gender and customer type
        String customerType = customerTypeRepository.findById(customer.getCustomerTypeId()).get().getName();
        String gender = categoryDataClient.getCategoryDataById(customer.getGenderId()).getCategoryName();
        customerDTO.setCustomerType(customerType);
        customerDTO.setGender(gender);

        customerDTO.setMemberCardDTO(memberCardDTO);

        return response.withData(customerDTO);
    }

    @Override
    public Response<Customer> getCustomerById(Long id) {
        Response<Customer> response = new Response<>();
        Customer customer = repository.getCustomerByIdAndDeletedAtIsNull(id);

        return response.withData(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> update(CustomerRequest request, Long userId) {

        Customer customerOld = repository.getCustomerByIdAndDeletedAtIsNull(request.getId());
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
        customerRecord = repository.save(customerRecord);

        return new Response<CustomerDTO>().withData(modelMapper.map(customerRecord, CustomerDTO.class));
    }

    @Override
    public Response<List<Response<CustomerDTO>>> deleteBulk(CustomerBulkDeleteRequest request, Long userId) {
        Response<List<Response<CustomerDTO>>> response = new Response<>();
        // TODO: check has company can not delete in list and throw error message
        List<Response<CustomerDTO>> resData = Arrays.stream(request.getCustomerIds())
                .map(aLong -> this.deleteCustomerById(aLong, userId))
                .collect(Collectors.toList());
        return response.withData(resData);
    }

    @Override
    public Response<Customer> getByIdAndType(Long id, Long typeId) {
        Response<Customer> response = new Response<>();
        Customer customer = repository.findByIdAndCustomerTypeId(id, typeId);

        return response.withData(customer);
    }


    /**
     * Delete company
     *
     * @param id
     * @return
     */
    private Response<CustomerDTO> deleteCustomerById(Long id, Long userId) {
        CustomerRequest request = new CustomerRequest();
        request.setId(id);
        return this.delete(request, userId);
    }


    /**
     * Delete a customer by way set delete at = date now
     *
     * @param request
     * @return
     */
    @Override
    @Transactional
    public Response<CustomerDTO> delete(CustomerRequest request, Long userId) {
        Customer customer = repository.getCustomerByIdAndDeletedAtIsNull(request.getId());
        if (customer == null) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        // TODO: just delete when not select cancel
        customer.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        Customer deleteRecord = repository.save(customer);
        return new Response<CustomerDTO>().withData(modelMapper.map(deleteRecord, CustomerDTO.class));
    }

}