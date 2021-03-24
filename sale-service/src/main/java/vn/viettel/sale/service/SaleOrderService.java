package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.CustomerDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.util.List;
public interface SaleOrderService {
    public Response<Page<SaleOrderDTO>> getAllSaleOrder(Pageable pageable);
    Response<List<SaleOrder>> getSaleOrders();
    Response<CustomerDTO> getCustomer(Long id);
}
