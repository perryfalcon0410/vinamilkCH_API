package vn.viettel.customer.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.messaging.CustomerBulkDeleteRequest;
import vn.viettel.customer.messaging.CustomerCreateRequest;
import vn.viettel.customer.messaging.CustomerDeleteRequest;
import vn.viettel.customer.messaging.CustomerUpdateRequest;
import vn.viettel.customer.repository.*;
import vn.viettel.customer.service.CustomerService;
import vn.viettel.customer.service.dto.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRepository> implements CustomerService {
    @Autowired
    CustomerRepository customerRepository;


    private Date date = new Date();
    private Timestamp dateTime = new Timestamp(date.getTime());

    @Override
    public Response<Page<CustomerResponse>> index(String searchKeywords, Date fromDate, Date toDate, Long groupId, Boolean status, Long gender, String areaAddress, Pageable pageable) {
        Response<Page<CustomerResponse>> response = new Response<>();
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        Page<Customer> customers;

        SimpleDateFormat oracleStyle = new SimpleDateFormat("dd-MMM-yy");
        String sFromDate = oracleStyle.format(fromDate);
        String sToDate = oracleStyle.format(toDate);

        customers = customerRepository.getAllCustomers(searchKeywords, sFromDate, sToDate, status, gender, pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<CustomerResponse> customerResponses = customers.map(this::mapCustomerToCustomerResponse);
        return response.withData(customerResponses);
    }

    private CustomerResponse mapCustomerToCustomerResponse(Customer customer) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CustomerResponse customerResponse = modelMapper.map(customer, CustomerResponse.class);
        return customerResponse;
    }


    @Override
    public Response<Customer> create(CustomerCreateRequest request, Long userId) {
        Optional<Customer> customer = customerRepository.getCustomerByCusCode(request.getCusCode());

        if(customer.isPresent()) {
            throw new ValidateException(ResponseMessage.CUSTOMER_CODE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Customer customerRecord =   modelMapper.map(request, Customer.class);
        customerRecord.setCreatedBy(userId);
        customerRecord = customerRepository.save(customerRecord);

        return new Response<Customer>().withData(customerRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> edit(Long id) {
        Response<CustomerDTO> response = new Response<>();

        Customer customer = customerRepository.getCustomerById(id);

        if(!customer.getId().equals(id)) {
            return response.withError(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);

        return response.withData(customerDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<CustomerDTO> update(CustomerUpdateRequest request, Long id, Long userId) {

        Customer customerOld = customerRepository.getCustomerById(request.getId());
        if (customerOld == null) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Customer customerNew = modelMapper.map(request, Customer.class);

        customerNew.setUpdatedBy(userId);
        customerNew.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        customerNew = customerRepository.save(customerNew);

        return new Response<CustomerDTO>().withData(modelMapper.map(customerNew, CustomerDTO.class));
    }

    @Override
    public Response<List<Response<CustomerDTO>>> deleteBulk(CustomerBulkDeleteRequest request) {
        Response<List<Response<CustomerDTO>>> response = new Response<>();
        // TODO: check has company can not delete in list and throw error message
        List<Response<CustomerDTO>> resData = Arrays.stream(request.getCustomerIds())
                .map(this::deleteCustomerById)
                .collect(Collectors.toList());
        return response.withData(resData);
    }


    /**
     * Delete company
     *
     * @param id
     * @return
     */
    private Response<CustomerDTO> deleteCustomerById(Long id) {
        CustomerDeleteRequest request = new CustomerDeleteRequest();
        request.setCustomerId(id);
        return this.delete(request);
    }


    /**
     * Delete a customer by way set delete at = date now
     *
     * @param request
     * @return
     */
    @Override
    @Transactional
    public Response<CustomerDTO> delete(CustomerDeleteRequest request) {
        Customer customer = customerRepository.getCustomerById(request.getCustomerId());
        if (customer == null) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }
        // TODO: just delete when not select cancel
        customer.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        Customer deleteRecord = customerRepository.save(customer);
        return new Response<CustomerDTO>().withData(modelMapper.map(deleteRecord, CustomerDTO.class));
    }


}
