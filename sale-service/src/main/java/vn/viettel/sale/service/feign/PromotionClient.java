package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.db.entity.promotion.PromotionSaleProduct;
import vn.viettel.core.db.entity.promotion.*;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.db.entity.voucher.VoucherSaleProduct;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import java.util.List;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface PromotionClient {
    @GetMapping("api/promotion/promotion-discount-program/{orderNumber}")
    Response<List<PromotionProgramDiscount>> listPromotionProgramDiscountByOrderNumber(@PathVariable(name = "orderNumber") String orderNumber);

    @GetMapping("api/promotion/promotion-program-by-id/{id}")
    Response<PromotionProgram> getById(@PathVariable(name = "id") Long id);

    @GetMapping("api/promotion/promotion-sale-product/{id}")
    Response<List<PromotionSaleProduct>> getPromotionSaleProductsByProductId(@PathVariable(name = "id") long id);

    @GetMapping("api/promotion/voucher/get-by-id/{id}")
    Response<Voucher> getVouchers(@PathVariable Long id);

    @GetMapping("api/voucher/voucher-sale_product/{voucherProgramId}")
    Response<List<VoucherSaleProduct>> findVoucherSaleProducts(@PathVariable Long voucherProgramId);

    @GetMapping("api/promotion/available-promotion-cus-attr/{shopId}")
    Response<List<PromotionCustATTR>> getGroupCustomerMatchProgram(@PathVariable Long shopId);

    @GetMapping("api/promotion/get-promotion-detail/{shopId}")
    Response<List<PromotionProgramDetail>> getPromotionDetailByPromotionId(@PathVariable Long shopId);

    @GetMapping("api/promotion/get-rejected-products")
    Response<List<PromotionProgramProduct>> getRejectProduct(@RequestParam List<Long> ids);

    @GetMapping("api/promotion/get-promotion-shop-map")
    Response<PromotionShopMap> getPromotionShopMap(@RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId);

    @PutMapping("api/promotion/save-change-promotion-shop-map")
    void saveChangePromotionShopMap(@RequestBody PromotionShopMap promotionShopMap,
                                    @RequestParam float amountReceived, @RequestParam Integer quantityReceived);
}
