package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.messaging.OrderReturnRequest;

@RestController
@RequestMapping("/api/sale/order-return")
public class OrderReturnController {
    @Autowired
    OrderReturnService orderReturnService;

    @GetMapping
    public Response<Page<OrderReturnDTO>> getAllOrderReturn(Pageable pageable) {
        return orderReturnService.getAllOrderReturn(pageable);
    }

    @GetMapping("/detail/{id}")
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(@PathVariable long id) {
        return orderReturnService.getOrderReturnDetail(id);
    }

    @RoleAdmin
    @PostMapping
    public Response<SaleOrder> createOrderReturn(@RequestBody OrderReturnRequest request) {
        return orderReturnService.createOrderReturn(request);
    }
}
