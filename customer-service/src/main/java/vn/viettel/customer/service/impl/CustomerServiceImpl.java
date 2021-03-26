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
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.messaging.CustomerBulkDeleteRequest;
import vn.viettel.customer.messaging.CustomerCreateRequest;
import vn.viettel.customer.messaging.CustomerDeleteRequest;
import vn.viettel.customer.messaging.CustomerUpdateRequest;
import vn.viettel.customer.repository.CategoryDataRepository;
import vn.viettel.customer.repository.CustomerRepository;
import vn.viettel.customer.repository.CustomerTypeRepository;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.*;
import vn.viettel.customer.service.feign.CommonClient;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRepository> implements CustomerService {

    @Autowired
    CommonClient commonClient;
    @Autowired
    CustomerTypeRepository customerTypeRepository;

    @Autowired
    CategoryDataRepository categoryDataRepository;


    @Override
    public Response<Page<CustomerDTO>> index(String searchKeywords, Date fromDate, Date toDate, Long customerTypeId, Long status, Long genderId, Long areaId, Pageable pageable) {
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
                .and(CustomerSpecification.hasAreaId(areaId)).and(CustomerSpecification.hasDeletedAtIsNull()), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<CustomerDTO> dtos = customers.map(this::mapCustomerToCustomerResponse);
        return response.withData(dtos);
    }

    private CustomerDTO mapCustomerToCustomerResponse(Customer customer) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);
        dto.setGender(categoryDataRepository.findById(customer.getGenderId()).getCategoryName());
        dto.setCustomerType(customerTypeRepository.findById(customer.getCustomerTypeId()).get().getName());
        return dto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Customer> create(CustomerCreateRequest request, Long userId) {
        Optional<Customer> customer = repository.getCustomerByCustomerCodeAndDeletedAtIsNull(request.getCustomerCode());

        if (customer.isPresent()) {
            throw new ValidateException(ResponseMessage.CUSTOMER_CODE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Customer customerRecord = modelMapper.map(request, Customer.class);
        // Created Identity Card
//        IdentityCardDTO identityCardDTO = identityCardService.create(request.getIdentityCard(), userId).getData();
//        customerRecord.setCreatedBy(userId);
//        customerRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        customerRecord.setUpdatedBy(userId);
//        customerRecord.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//        customerRecord.setIdentityCardId(identityCardDTO.getId());
        customerRecord = repository.save(customerRecord);

        return new Response<Customer>().withData(customerRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> edit(Long id) {
        Response<CustomerDTO> response = new Response<>();

        Customer customer = repository.getCustomerByIdAndDeletedAtIsNull(id);

        if (!customer.getId().equals(id)) {
            return response.withError(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);

        return response.withData(customerDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> update(CustomerUpdateRequest request, Long id, Long userId) {

        Customer customerOld = repository.getCustomerByIdAndDeletedAtIsNull(request.getId());
        if (customerOld == null) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Customer customerRecord = modelMapper.map(request, Customer.class);
        // Created Identity Card
//        request.getIdentityCard().setId(customerOld.getIdentityCardId());
//        IdentityCardDTO identityCardDTO = identityCardService.update(request.getIdentityCard(), userId).getData();
//        customerRecord.setIdentityCardId(identityCardDTO.getId());
//        customerRecord.setCreatedAt(customerOld.getCreatedAt());
//        customerRecord.setCreatedBy(customerOld.getCreatedBy());
//        customerRecord.setUpdatedBy(userId);
        customerRecord.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
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
    public Response<List<LocationDTO>> getAllLocationOfCustomers(Long shopId) {
        Response<List<LocationDTO>> response = new Response<>();

        List<Customer> customers = repository.getCustomersByShopId(shopId);

//        List<Long> provinceIds = customers.stream().map(Customer::getProvinceId).collect(Collectors.toList());
//
//        List<ProvinceDTO> provinceDTOS = commonClient.getAllProvinceByIds(provinceIds).getData();
//
//        List<Long> districtIds = customers.stream().map(Customer::getDistrictId).collect(Collectors.toList());
//        List<DistrictDTO> districtDTOS = commonClient.getAllDistrictByIds(districtIds).getData();
//
//        List<LocationDTO> dtos = customers.stream().map(customer -> {
//            Optional<ProvinceDTO> provinceDTO = provinceDTOS.stream().filter(e -> e.getId().equals(customer.getProvinceId())).findFirst();
//            Optional<DistrictDTO> districtDTO = districtDTOS.stream().filter(e -> e.getId().equals(customer.getDistrictId())).findFirst();
//            return new LocationDTO(provinceDTO.get().getName() + " - " + districtDTO.get().getName(), provinceDTO.get().getId() + "," + districtDTO.get().getId());
//        }).collect(Collectors.toList());
//
//        dtos = dtos.stream().distinct().collect(Collectors.toList());

        return response;
    }


    /**
     * Delete company
     *
     * @param id
     * @return
     */
    private Response<CustomerDTO> deleteCustomerById(Long id, Long userId) {
        CustomerDeleteRequest request = new CustomerDeleteRequest();
        request.setCustomerId(id);
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
    public Response<CustomerDTO> delete(CustomerDeleteRequest request, Long userId) {
        Customer customer = repository.getCustomerByIdAndDeletedAtIsNull(request.getCustomerId());
        if (customer == null) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        // TODO: just delete when not select cancel
        customer.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        Customer deleteRecord = repository.save(customer);
        return new Response<CustomerDTO>().withData(modelMapper.map(deleteRecord, CustomerDTO.class));
    }

    //sonpht
    public Response<Customer> getCustomerById(Long id) {
        Customer customer = customerRepository.getCustomerById(id);
        Response<Customer> response = new Response<>();
        response.setData(customer);
        return response;
    }

}
