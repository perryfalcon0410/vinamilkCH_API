package vn.viettel.sale.service.feign;


import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.dto.promotion.*;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.messaging.PromotionProductRequest;
import vn.viettel.core.messaging.RPT_ZV23Request;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @PutMapping("api/v1/promotions/vouchers")
    Response<VoucherDTO> updateVoucherV1(@RequestBody VoucherDTO request);

    @GetMapping("api/v1/promotions/vouchers/get-by-sale-order-id/{id}")
    Response<List<VoucherDTO>> getVoucherBySaleOrderIdV1(@PathVariable Long id);

    @GetMapping("api/v1/promotions/vouchers/voucher-sale_product/{voucherProgramId}")
    Response<List<VoucherSaleProductDTO>> findVoucherSaleProductsV1(@PathVariable Long voucherProgramId);

    @GetMapping("api/v1/promotions/available-promotion-cus-attr/{shopId}")
    Response<List<PromotionCustATTRDTO>> getGroupCustomerMatchProgramV1(@PathVariable Long shopId);

    @GetMapping("api/v1/promotions/get-promotion-detail/{shopId}")
    Response<List<PromotionProgramDetailDTO>> getPromotionDetailByPromotionIdV1(@PathVariable Long shopId);

    @GetMapping("api/v1/promotions/getzv23products")
    Response<List<PromotionProgramProductDTO>> findByPromotionIdsV1(@RequestParam List<Long> promotionIds);

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

    @GetMapping(value = {"api/v1/promotions/buying-condition"})
    Long checkBuyingCondition(@RequestParam String type, @RequestParam Integer quantity, @RequestParam Double amount, @RequestParam List<Long> ids);

    @GetMapping(value = {"api/v1/promotions/required-products"})
    List<Long> getRequiredProducts(@RequestParam String type);

    @GetMapping(value = {"api/v1/promotions/promotion-programs/shop/{id}"})
    Response<List<PromotionProgramDTO>> findPromotionPrograms(@PathVariable Long id, @RequestParam Long orderType, @RequestParam Long customerTypeId,
                                                              @RequestParam Long memberCard, @RequestParam Long cusCloselyTypeId, @RequestParam Long cusCardTypeId);

    @GetMapping("/api/v1/promotions/promotion-cust-attr/{programId}")
    Response<Set<Long>> findCusCardPromotion(@PathVariable Long programId, @RequestParam Integer objectType);

    @GetMapping(value = { "/api/v1/promotions/promotion-program-detail/{programId}"})
    Response<List<PromotionProgramDetailDTO>> findPromotionProgramDetailV1(@PathVariable Long programId);

    @GetMapping(value = { "/api/v1/promotions/promotion-sale-product/{programId}"})
    Response<List<PromotionSaleProductDTO>> findPromotionSaleProductByProgramIdV1(@PathVariable Long programId);

    @GetMapping(value = { "/api/v1/promotions/promotion-discount/{programId}"})
    Response<List<PromotionProgramDiscountDTO>> findPromotionDiscountByPromotion(@PathVariable Long programId);

    @GetMapping(value = {"/api/v1/promotions/RPT-ZV23/promotion-checkZV23"})
    Response<RPT_ZV23DTO> checkZV23RequireV1(@RequestParam String promotionCode,@RequestParam Long customerId,@RequestParam Long shopId);

    @GetMapping(value = {"/api/v1/promotions/promotion-item-product/not-accumlated"})
    Response<List<Long>> getProductsNotAccumulatedV1(@RequestBody List<Long> productIds);

    @PutMapping(value = { "/api/v1/promotions/promotion-program-discount"})
    Response<PromotionProgramDiscountDTO> updatePromotionProgramDiscountV1(@RequestBody PromotionProgramDiscountDTO discount);

    @PutMapping(value = {"/api/v1/promotions/promotion-shop-map"})
    Response<PromotionShopMapDTO> updatePromotionShopMapV1(@Valid @RequestBody PromotionShopMapDTO shopmap);

    @PutMapping(value = {"/api/v1/promotions/RPT-ZV23/{id}"})
    Response<Boolean> updateRPTZV23V1(@PathVariable Long id, @RequestBody RPT_ZV23Request request);

    @PutMapping(value = {"/api/v1/promotions/create/RPT-ZV23"})
    Response<Boolean> createRPTZV23V1(@RequestBody RPT_ZV23Request request);

    @GetMapping(value = {"/api/v1/promotions/promotion-program-discount/code/{code}"})
    Response<PromotionProgramDiscountDTO> getPromotionDiscount(@PathVariable("code") String discountCode, @RequestParam Long shopId);

}
