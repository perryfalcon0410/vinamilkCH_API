package vn.viettel.sale.service.feign;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.viettel.core.db.entity.promotion.PromotionProgram;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
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
    @GetMapping("api/voucher/get-by-id/{id}")
    Response<Voucher> getVouchers(@PathVariable Long id);
    @GetMapping("api/voucher/voucher-sale_product/{voucherProgramId}")
    Response<List<VoucherSaleProduct>> findVoucherSaleProducts(@PathVariable Long voucherProgramId);
}
