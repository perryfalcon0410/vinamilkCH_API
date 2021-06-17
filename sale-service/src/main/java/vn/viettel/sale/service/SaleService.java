package vn.viettel.sale.service;

import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.ProductOrderRequest;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.service.dto.PrintSaleOrderDTO;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import java.util.List;

public interface SaleService {
    /*
    create sale order return order id
     */
    Object createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId, boolean printTemp);

    /*
    Cập nhật doanh số mua cho khách hàng
     */
    void updateCustomerTotalBill(Double customerPurchase, CustomerDTO customer);

    /*
    câp nhật giá trị chiết tích lũy
     */
    void updateAccumulatedAmount(Double accumulatedAmount, Long customerId, Long shopId);
}
