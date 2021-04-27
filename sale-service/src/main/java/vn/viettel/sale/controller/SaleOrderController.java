package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;

import java.util.List;

@RestController
public class SaleOrderController extends BaseController {
    @Autowired
    SaleOrderService saleOrderService;
    private final String root = "/sales/sale-orders";

    @RoleAdmin
    @GetMapping(value = { V1 + root })
    public Response<CoverResponse<Page<SaleOrderDTO>, SaleOrderTotalResponse>> getAllSaleOrder(Pageable pageable) {
        return saleOrderService.getAllSaleOrder(pageable);
    }
    @RoleAdmin
    @GetMapping(value = { V1 + root + "/detail"})
    public Response<SaleOrderDetailDTO> getSaleOrderDetail(@RequestParam long saleOrderId,
                                                           @RequestParam String orderNumber) {
        return saleOrderService.getSaleOrderDetail(saleOrderId, orderNumber);
    }

    @GetMapping(V1 + root +"/last-sale-order/{id}")
    Response<SaleOrderDTO> GetLastSaleOrderByCustomerId(@PathVariable("id") Long id){
        return saleOrderService.getLastSaleOrderByCustomerId(id);
    }
}
