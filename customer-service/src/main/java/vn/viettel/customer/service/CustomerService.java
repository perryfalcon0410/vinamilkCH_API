package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.customer.service.dto.*;

import java.util.List;
import java.util.Map;

public interface CustomerService extends BaseService {

    CustomerDTO create(CustomerRequest customerRequest, Long userId, Long shopId);

    CustomerDTO getCustomerById(Long id);

    List<CustomerDTO> getCustomerByMobiPhone(String phone );

    CustomerDTO update(CustomerRequest request, Long userId);

    Page<CustomerDTO> index(CustomerFilter filter, Pageable pageable);

    Page<CustomerDTO> getAllCustomerToSaleService(String searchKeywords, Pageable pageable);

    List<Long> getIdCustomerBySearchKeyWords(String searchKeywords);

    List<Long> getIdCustomerBy(String searchKeywords, String customerPhone);

    List<ExportCustomerDTO> findAllCustomer(CustomerFilter customerFilter);

    CustomerDTO getCustomerDefault(Long shopId);

    CustomerDTO getCustomerDefaultByShop(Long shopId);

    /*
    Lấy danh sách thông tin kh theo danh sách id
     */
    List<CustomerDTO> getCustomerInfo(Integer status, List<Long> customerIds);

    /*
    Lấy danh sách khách hàng. Tối ưu ko gọi db sd trong export excel red invoice
     */
    Map<Integer, CustomerDTO> getAllCustomerToRedInvoice();
}

