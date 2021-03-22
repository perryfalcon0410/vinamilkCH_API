package vn.viettel.sale.service;

import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.CustomerDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.util.List;
public interface SaleOrderService {
    Response<List<SaleOrderDTO>> getAllSaleOrder();
    Response<List<SaleOrder>> getSaleOrders();
    Response<CustomerDTO> getCustomer(Long id);
}
