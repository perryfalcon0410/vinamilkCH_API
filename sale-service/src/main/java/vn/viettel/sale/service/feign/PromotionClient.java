package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import javax.validation.Valid;
import java.util.List;

@Service
@FeignClientAuthenticate(name = "promotion-service")
public interface PromotionClient {
    @GetMapping("/api/v1/promotions/promotion-program-discount/{orderNumber}")
    Response<List<PromotionProgramDiscountDTO>> listPromotionProgramDiscountByOrderNumberV1(@PathVariable String orderNumber);

    @GetMapping("api/v1/promotions/{id}")
    Response<PromotionProgramDTO> getByIdV1(@PathVariable Long id);

    @GetMapping("api/v1/promotions")
    Response<PromotionProgramDTO> getByCodeV1(@RequestParam String code);

    @GetMapping("api/v1/promotions/promotion-sale-product/{id}")
    Response<List<PromotionSaleProductDTO>> getPromotionSaleProductsByProductIdV1(@PathVariable long id);

    @GetMapping("api/v1/promotions/vouchers/feign/{id}")
    Response<VoucherDTO> getVouchersV1(@PathVariable Long id);

    @GetMapping("api/v1/promotions/vouchers/get-by-sale-order-id/{id}")
    Response<List<VoucherDTO>> getVoucherBySaleOrderIdV1(@PathVariable Long id);

    @GetMapping("api/v1/promotions/vouchers/voucher-sale_product/{voucherProgramId}")
    Response<List<VoucherSaleProductDTO>> findVoucherSaleProductsV1(@PathVariable Long voucherProgramId);

    @GetMapping("api/v1/promotions/available-promotion-cus-attr/{shopId}")
    Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgramV1(@PathVariable Long shopId);

    @GetMapping("api/v1/promotions/get-promotion-detail/{shopId}")
    Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionIdV1(@PathVariable Long shopId);

    @GetMapping("api/v1/promotions/get-rejected-products")
    Response<List<PromotionProgramProductDTO>> getRejectProductV1(@RequestParam List<Long> ids);

    @GetMapping("api/v1/promotions/get-promotion-shop-map")
    Response<PromotionShopMapDTO> getPromotionShopMapV1(@RequestParam Long promotionProgramId,
                                                          @RequestParam Long shopId);

    @PutMapping("api/v1/promotions/save-change-promotion-shop-map")
    void saveChangePromotionShopMapV1(@RequestParam Long promotionProgramId,
                                      @RequestParam Long shopId, @RequestParam Double receivedQuantity);

    @GetMapping("api/v1/promotions/get-zm-promotion")
    Response<List<PromotionSaleProductDTO>> getZmPromotionV1(@RequestParam Long productId);

    @PostMapping("api/v1/promotions/get-free-items/{programId}")
    Response<List<PromotionProductOpenDTO>> getFreeItemV1(@PathVariable Long programId);

    @GetMapping("api/v1/promotions/get-promotion-discount")
    Response<List<PromotionProgramDiscountDTO>> getPromotionDiscountV1(@RequestParam List<Long> ids, @RequestParam String cusCode);

    @PutMapping(value = { "api/v1/promotions/vouchers"})
    Response<VoucherDTO> updateVoucher(@Valid @RequestBody VoucherDTO request);

    @GetMapping(value = {"api/v1/promotions/isReturn"})
    Boolean isReturn(@RequestParam String code);

    @GetMapping (value = {"api/v1/promotions/discount-percent"})
    Double getDiscountPercent(@RequestParam String type, @RequestParam String code, @RequestParam Double amount);
}
