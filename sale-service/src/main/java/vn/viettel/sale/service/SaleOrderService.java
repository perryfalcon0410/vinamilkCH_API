package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.dto.OrderDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;
import vn.viettel.sale.service.dto.SaleOrderDetailDTO;

import java.util.Date;
import java.util.List;
public interface SaleOrderService {
    Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>> getAllSaleOrder(Pageable pageable);
    Response<SaleOrderDetailDTO> getSaleOrderDetail(long saleOrderId, String orderNumber);
    Response<Page<SaleOrderDTO>> getAllBillOfSaleList(String searchKeywords, String invoiceNumber, Date fromDate, Date toDate, Pageable pageable);
    Response<SaleOrderDTO> getLastSaleOrderByCustomerId(Long customerId);
}
