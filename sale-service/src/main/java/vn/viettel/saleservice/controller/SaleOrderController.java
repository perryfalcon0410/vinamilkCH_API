package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.SaleOrderService;
import vn.viettel.saleservice.service.dto.CustomerResponse;
import vn.viettel.saleservice.service.dto.SaleOrderDTO;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class SaleOrderController {
    @Autowired
    SaleOrderService saleOrderService;

    @GetMapping("/sale-order/all-sale-orders")
    public Response<List<SaleOrderDTO>> getAllSaleOrder() {
        return saleOrderService.getAllSaleOrder();
    }
    @GetMapping("/sale-order/get-sale-orders")
    public Response<List<SaleOrder>> getSaleOrders() {
        return saleOrderService.getSaleOrders();
    }
    @GetMapping("/sale-order/get-cus/{id}")
    public Response<CustomerResponse> getCus(@PathVariable long id) {
        return saleOrderService.getCus(id);
    }
}
