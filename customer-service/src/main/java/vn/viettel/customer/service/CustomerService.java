package vn.viettel.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.SortDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CustomerOnlRequest;
import vn.viettel.core.messaging.CustomerRequest;
import vn.viettel.core.service.BaseService;
import vn.viettel.customer.messaging.CusRedInvoiceFilter;
import vn.viettel.customer.messaging.CustomerFilter;
import vn.viettel.customer.messaging.CustomerSaleFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface CustomerService extends BaseService {

    CustomerDTO create(CustomerRequest customerRequest, Long userId, Long shopId);

    CustomerDTO createForOnlOrder(CustomerOnlRequest customerRequest, Long shopId);

    CustomerDTO getCustomerById(Long id,Long shopId);

    List<CustomerDTO> getCustomerByMobiPhone(String phone );

    CustomerDTO update(CustomerRequest request, Long userId,Long shopId, Boolean checkUpdate);

    Page<CustomerDTO> index(CustomerFilter filter, Pageable pageable);

   Page<CustomerDTO> getCustomerForAutoComplete(String searchKeywords, Pageable pageable);

    List<Long> getIdCustomerBySearchKeyWords(String searchKeywords);

    List<Long> getIdCustomerBy(String searchKeywords, String customerPhone);

    ByteArrayInputStream exportExcel(CustomerFilter filter) throws IOException;

    CustomerDTO getCustomerDefault(Long shopId);

    CustomerDTO getCustomerDefaultByShop(Long shopId);

    /*
    Lấy danh sách thông tin kh theo danh sách id
     */
    List<CustomerDTO> getCustomerInfo(Integer status, List<Long> customerIds, List<SortDTO> sorts);

    /*
    Lấy danh sách khách hàng. Tối ưu ko gọi db sd trong export excel red invoice
     */
//    List<CustomerDTO> getAllCustomerToRedInvoice(List<Long> customerIds);

    /*
    Update khách hàng trong bán hàng
     */
    CustomerDTO updateForSale(CustomerRequest request, Long shopId);

     void updateCustomerStartDay();

     void updateCustomerStartMonth();

     /*
     Lấy thông tin tiền tích lũy
      */
    Double getScoreCumulated(Long customerId);

    /*
    Lấy thông tin khách hàng khi auto search
     */
    Page<CustomerDTO> findCustomerForSale(Long shopId, CustomerSaleFilter customerFilter, Pageable pageable);

    /*
   Lấy thông tin khách hàng cho màn hình tạo mới hóa đơn đỏ
    */
    Page<CustomerDTO> findCustomerForRedInvoice(CusRedInvoiceFilter filter, Pageable pageable);


}

