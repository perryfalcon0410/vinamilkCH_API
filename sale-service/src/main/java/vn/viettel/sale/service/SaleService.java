package vn.viettel.sale.service;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.customer.CustomerDTO;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.SaleOrderRequest;
import vn.viettel.sale.service.dto.OnlineOrderValidDTO;

public interface SaleService {
    /*
    create sale order return order id
     */
    Object createSaleOrder(SaleOrderRequest request, long userId, long roleId, long shopId, boolean printTemp);

    /*
    Cập nhật doanh số mua cho khách hàng
     */
    void updateCustomer(SaleOrder saleOrder, CustomerDTO customer, boolean saleOrderReturn);

    /*
    câp nhật giá trị chiết tích lũy
     */
    void updateAccumulatedAmount(Double accumulatedAmount, Long customerId);


    /*
    Tạo mã đơn
    */
    String createOrderNumber(ShopDTO shopDTO);

    OnlineOrderValidDTO getValidOnlineOrder(Long shopId);
}
