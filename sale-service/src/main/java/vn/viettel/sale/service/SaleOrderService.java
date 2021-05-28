package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.RedInvoiceFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.dto.PrintSaleOrderDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.dto.SaleOrderDetailDTO;

public interface SaleOrderService {
    CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse> getAllSaleOrder(SaleOrderFilter saleOrderFilter, Pageable pageable, Long id);
    SaleOrderDetailDTO getSaleOrderDetail(long saleOrderId, String orderNumber);
    Page<SaleOrderDTO> getAllBillOfSaleList(RedInvoiceFilter redInvoiceFilter,Long shopId, Pageable pageable);
    Response<SaleOrderDTO> getLastSaleOrderByCustomerId(Long customerId);
    PrintSaleOrderDTO printSaleOrder (Long id, Long shopId);
}
