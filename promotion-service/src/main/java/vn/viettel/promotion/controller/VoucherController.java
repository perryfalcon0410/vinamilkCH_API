package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.voucher.Voucher;
import vn.viettel.core.db.entity.voucher.VoucherSaleProduct;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.messaging.VoucherFilter;
import vn.viettel.promotion.messaging.VoucherUpdateRequest;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.dto.VoucherDTO;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/promotion/vouchers")
public class VoucherController extends BaseController {

    @Autowired
    VoucherService voucherService;

    // find vouchers for sale
    @RoleAdmin
    @RoleFeign
    @GetMapping
    public Response<Page<VoucherDTO>> findVouchers(@RequestParam( name = "keyWord", defaultValue = "") String keyWord,
                                                   @RequestParam("customerTypeId") Long customerTypeId, Pageable pageable) {
        VoucherFilter voucherFilter = new VoucherFilter(keyWord, customerTypeId, this.getShopId());
        return voucherService.findVouchers(voucherFilter, pageable);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping("/{id}")
    public Response<Voucher> getVouchers(@PathVariable Long id) {
        return voucherService.getVoucher(id);
    }

    @RoleAdmin
    @RoleFeign
    @PatchMapping("/{id}")
    public Response<VoucherDTO> updateVoucher(@PathVariable Long id,
                                              @Valid @RequestBody VoucherUpdateRequest request) {
        return voucherService.updateVoucher(id, request, this.getUserId());
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping("/voucher-sale-products/{voucherProgramId}")
    public Response<List<VoucherSaleProduct>> findVoucherSaleProducts(@PathVariable Long voucherProgramId) {
        return voucherService.findVoucherSaleProducts(voucherProgramId);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping("/get-by-sale-order-id/{id}")
    public Response<List<Voucher>> getVoucherBySaleOrderId(@PathVariable Long id) {
        return voucherService.getVoucherBySaleOrderId(id);
    }
}
