package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.*;

import java.util.List;
public interface SaleOrderService {
    Response<Page<SaleOrderDTO>> getAllSaleOrder(Pageable pageable);
    Response<SaleOrderDetailDTO> getSaleOrderDetail(GetOrderDetailRequest request);

    Response<List<SaleOrder>> getSaleOrders();
    Response<CustomerDTO> getCustomerDTO(Long id);
    Response<List<PromotionProgramDiscount>> getListPromotion(String orderNumber);

    Response<List<OrderDetailDTO>> getDetail(long saleOrderId);
}
