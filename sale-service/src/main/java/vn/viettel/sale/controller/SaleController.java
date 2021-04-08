package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.OrderDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/sale/sale")
public class SaleController extends BaseController {
    @Autowired
    SaleService service;

    @RoleAdmin
    @PostMapping
    public Response<SaleOrder> createSaleOrder(@Valid @RequestBody SaleOrderRequest request,
                                               @RequestParam  long formId,
                                               @RequestParam  long ctrlId) {
        return service.createSaleOrder(request, this.getUserId(), this.getRoleId(), this.getShopId(), formId, ctrlId);
    }

    @RoleAdmin
    @GetMapping("/shop/{id}")
    public Response<Shop> getShopById(@PathVariable long id) {
        return service.getShopById(id);
    }

    @RoleAdmin
    @PostMapping("/promotion-free-item")
    public Response<List<ZmFreeItemDTO>> getFreeItems(@RequestBody List<OrderDetailDTO> productList) {
        return service.getFreeItems(productList);
    }
}
