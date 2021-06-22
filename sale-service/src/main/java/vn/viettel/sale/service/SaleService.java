package vn.viettel.sale.service;

import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.sale.messaging.SaleOrderRequest;

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
