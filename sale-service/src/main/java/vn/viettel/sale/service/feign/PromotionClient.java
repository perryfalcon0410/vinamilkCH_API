package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface PromotionClient {
    @GetMapping("api/promotion/promotion-program-discount/{orderNumber}")
    Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumber(@PathVariable String orderNumber);

    @GetMapping("api/promotion/{id}")
    Response<PromotionProgramDTO> getById(@PathVariable Long id);

    @GetMapping("api/promotion/promotion-sale-product/{id}")
    Response<List<PromotionSaleProductDTO>> getPromotionSaleProductsByProductId(@PathVariable long id);

    @GetMapping("api/promotion/vouchers/feign/{id}")
    Response<VoucherDTO> getVouchers(@PathVariable Long id);

    @GetMapping("api/promotion/vouchers/get-by-sale-order-id/{id}")
    Response<List<VoucherDTO>> getVoucherBySaleOrderId(@PathVariable Long id);

    @GetMapping("api/promotion/vouchers/voucher-sale_product/{voucherProgramId}")
    Response<List<VoucherSaleProductDTO>> findVoucherSaleProducts(@PathVariable Long voucherProgramId);

    @GetMapping("api/promotion/available-promotion-cus-attr/{shopId}")
    Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgram(@PathVariable Long shopId);

    @GetMapping("api/promotion/get-promotion-detail/{shopId}")
    Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionId(@PathVariable Long shopId);

    @GetMapping("api/promotion/get-rejected-products")
    Response<List<PromotionProgramProductDTO>> getRejectProduct(@RequestParam List<Long> ids);

    @GetMapping("api/promotion/get-promotion-shop-map")
    Response<PromotionShopMapDTO> getPromotionShopMap(@RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId);

    @PutMapping("api/promotion/save-change-promotion-shop-map")
    void saveChangePromotionShopMap(@RequestBody PromotionShopMapDTO promotionShopMap,
                                    @RequestParam float amountReceived, @RequestParam Integer quantityReceived);

    @GetMapping("api/promotion/get-zm-promotion")
    Response<List<PromotionSaleProductDTO>> getZmPromotion(@RequestParam Long productId);

    @PostMapping("api/promotion/get-free-items/{programId}")
    Response<List<PromotionProductOpenDTO>> getFreeItem(@PathVariable Long programId);

    @GetMapping("api/promotion/get-promotion-discount")
    Response<List<PromotionProgramDiscountDTO>> getPromotionDiscount(@RequestParam List<Long> ids, @RequestParam String cusCode);
}
