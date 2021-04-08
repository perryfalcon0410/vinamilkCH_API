package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale/sale-order")
public class SaleOrderController {
    @Autowired
    SaleOrderService saleOrderService;

    @GetMapping
    public Response<Page<SaleOrderDTO>> getAllSaleOrder(Pageable pageable) {
        return saleOrderService.getAllSaleOrder(pageable);
    }
    @GetMapping("/sale-order-detail")
    public Response<SaleOrderDetailDTO> getSaleOrderDetail(@RequestBody GetOrderDetailRequest request) {
        return saleOrderService.getSaleOrderDetail(request);
    }
    @GetMapping("/detail/{soId}")
    public Response<List<OrderDetailDTO>> getListOrderDetail(@PathVariable Long soId) {
        return saleOrderService.getDetail(soId);
    }
    @GetMapping("/last-sale-order/{id}")
    public Response<SaleOrder> getLastSaleOrderByCustomerId(@PathVariable("id") Long id) {
        return saleOrderService.getLastSaleOrderByCustomerId(id);
    }
}
