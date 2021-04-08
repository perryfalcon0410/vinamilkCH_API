package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;

@RestController
@RequestMapping("/api/sale")
public class OrderReturnController {
    @Autowired
    OrderReturnService orderReturnService;

    @GetMapping("/order-return/all-orders-return")
    public Response<Page<OrderReturnDTO>> getAllSaleOrder(Pageable pageable) {
        return orderReturnService.getAllOrderReturn(pageable);
    }

    @GetMapping("/order-return/order-return-detail/{orderReturnId}")
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(@PathVariable long orderReturnId) {
        return orderReturnService.getOrderReturnDetail(orderReturnId);
    }
}