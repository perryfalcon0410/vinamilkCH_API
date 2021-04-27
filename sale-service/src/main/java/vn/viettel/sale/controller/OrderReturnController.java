package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.OrderReturnTotalResponse;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.messaging.OrderReturnRequest;

@RestController
public class OrderReturnController extends BaseController {
    @Autowired
    OrderReturnService orderReturnService;
    private final String root = "/sales/order-return";
    @RoleAdmin
    @GetMapping(value = { V1 + root } )
    public Response<CoverResponse<Page<OrderReturnDTO>, OrderReturnTotalResponse>> getAllOrderReturn(Pageable pageable) {
        return orderReturnService.getAllOrderReturn(pageable);
    }
    @RoleAdmin
    @GetMapping(value = { V1 + root + "/detail/{id}"})
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(@PathVariable long id) {
        return orderReturnService.getOrderReturnDetail(id);
    }

    @RoleAdmin
    @PostMapping(value = { V1 + root })
    public Response<SaleOrder> createOrderReturn(@RequestBody OrderReturnRequest request) {
        return orderReturnService.createOrderReturn(request);
    }
}
