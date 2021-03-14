package vn.viettel.saleservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.db.entity.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.SaleService;
import vn.viettel.saleservice.service.dto.SaleOrderRequest;

import javax.validation.Valid;

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
}
