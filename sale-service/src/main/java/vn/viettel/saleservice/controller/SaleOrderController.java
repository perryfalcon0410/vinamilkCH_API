package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.SaleOrderService;
import vn.viettel.saleservice.service.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/so")
public class SaleOrderController {
    @Autowired
    SaleOrderService saleOrderService;

    @GetMapping("/all-sale-orders")
    public Response<List<SaleOrderDTO>> getAllSaleOrder() {
        return saleOrderService.getAllSaleOrder();
    }
    @GetMapping("/get-sale-orders")
    public Response<List<SaleOrder>> getSaleOrders() {
        return saleOrderService.getSaleOrders();
    }
}
