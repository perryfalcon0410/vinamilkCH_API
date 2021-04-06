package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.SaleOrderService;
import vn.viettel.sale.service.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale")
public class SaleOrderController {
    @Autowired
    SaleOrderService saleOrderService;

    @GetMapping("/sale-order/all-sale-orders")
    public Response<Page<SaleOrderDTO>> getAllSaleOrder(Pageable pageable) {
        return saleOrderService.getAllSaleOrder(pageable);
    }
    @GetMapping("/sale-order/sale-order-detail")
    public Response<SaleOrderDetailDTO> getSaleOrderDetail(@RequestBody GetOrderDetailRequest request) {
        return saleOrderService.getSaleOrderDetail(request);
    }
    @GetMapping("/sale-order/get-sale-orders")
    public Response<List<SaleOrder>> getSaleOrders() {
        return saleOrderService.getSaleOrders();
    }

    @GetMapping("/sale-order/get-customer-by-id/{id}")
    public Response<Customer> getCustomerById(@PathVariable Long id) {
        return saleOrderService.getCustomerById(id);
    }

    @GetMapping("/sale-order/get-promotion-by-order-number/{orderNumber}")
    public Response<List<PromotionProgramDiscount>> getPromotion(@PathVariable String orderNumber) {
        return saleOrderService.getListPromotion(orderNumber);
    }

    @GetMapping("/sale-order/get-list-order-detail/{soId}")
    public Response<List<OrderDetailDTO>> getListOrderDetail(@PathVariable Long soId) {
        return saleOrderService.getDetail(soId);
    }

    @GetMapping("/sale-order/get-voucher/{Id}")
    Response<List<Voucher>> getById(@PathVariable Long Id) {
        return saleOrderService.getById(Id);
    }

    @GetMapping("/sale-order/last-sale-order/{id}")
    public Response<SaleOrder> GetLastSaleOrderByCustomerId(@PathVariable("id") Long id) {
        return saleOrderService.getLastSaleOrderByCustomerId(id);
    }
}
