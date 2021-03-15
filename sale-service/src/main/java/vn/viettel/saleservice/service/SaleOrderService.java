package vn.viettel.saleservice.service;

import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.CustomerResponse;
import vn.viettel.saleservice.service.dto.SaleOrderDTO;

import java.util.List;
public interface SaleOrderService {
    public Response<List<SaleOrderDTO>> getAllSaleOrder();
    public Response<List<SaleOrder>> getSaleOrders();
    public Response<CustomerResponse> getCus(long id);
//    public Response<List<SaleOrderDetailDTO>> getAllSaleOrderDetail();
}
