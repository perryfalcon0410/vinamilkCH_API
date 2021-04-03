package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.dto.OrderDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderRequest;
import vn.viettel.sale.service.dto.ZmFreeItemDTO;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/sale")
public class SaleController {
    @Autowired
    SaleService service;

    @PostMapping("/create-sale-order/{userId}")
    public Response<SaleOrder> createSaleOrder(@Valid @RequestBody SaleOrderRequest request, @PathVariable  long userId) {
        return service.createSaleOrder(request, userId);
    }

    @GetMapping("/get-shop-by-id/{id}")
    public Response<Shop> getShopById(@PathVariable long id) {
        return service.getShopById(id);
    }

    @PostMapping("/get-promotion-free-item")
    public Response<List<ZmFreeItemDTO>> getFreeItems(@RequestBody List<OrderDetailDTO> productList) {
        return service.getFreeItems(productList);
    }
}
