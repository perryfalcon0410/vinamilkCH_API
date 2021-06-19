package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.SaleOrderDetail;
import vn.viettel.sale.entities.SaleOrderDiscount;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.dto.PrintSaleOrderDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.dto.SaleOrderDetailDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleOrderService {
    CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> getAllSaleOrder(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id);
    SaleOrderDetailDTO getSaleOrderDetail(long saleOrderId, String orderNumber);
    Page<SaleOrderDTO> getAllBillOfSaleList(RedInvoiceFilter redInvoiceFilter,Long shopId, Pageable pageable);
    SaleOrderDTO getLastSaleOrderByCustomerId(Long customerId);

    Double getTotalBillForTheMonthByCustomerId(Long customerId, LocalDateTime lastOrderDate);

    /*
    In hóa đơn bán hàng
     */
    PrintSaleOrderDTO printSaleOrder (Long id, Long shopId);

    /*
    Lấy thông tin để in hóa đơn bán hàng
     */
    PrintSaleOrderDTO createPrintSaleOrderDTO(Long shopId, CustomerDTO customer, SaleOrder saleOrder
            , List<SaleOrderDetail> lstSaleOrderDetail, List<SaleOrderDiscount> lstSaleOrderDiscount);
}
