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
import vn.viettel.promotion.messaging.VoucherUpdateRequest;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.dto.VoucherDTO;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/promotion/voucher")
public class VoucherController extends BaseController {

    @Autowired
    VoucherService voucherService;

    // find vouchers for sale
    @RoleAdmin
    @GetMapping("/find")
    public Response<Page<VoucherDTO>> findVouchers(@RequestParam( name = "keyWord", defaultValue = "") String keyWord,
                                                   @RequestParam("shopId") Long shopId,
                                                   @RequestParam("customerTypeId") Long customerTypeId, Pageable pageable) {
        return voucherService.findVouchers(keyWord, shopId, customerTypeId, pageable);
    }

    @GetMapping("/get-by-id/{id}")
    public Response<Voucher> getVouchers(@PathVariable Long id) {
        return voucherService.getVoucher(id);
    }

    @PatchMapping("/update/{id}")
    public Response<VoucherDTO> updateVoucher(@PathVariable Long id,
                                              @RequestParam("username") String username,
                                              @Valid @RequestBody VoucherUpdateRequest request) {
        return voucherService.updateVoucher(id, request, username);
    }

    @GetMapping("/voucher-sale_product/{voucherProgramId}")
    public Response<List<VoucherSaleProduct>> findVoucherSaleProducts(@PathVariable Long voucherProgramId) {
        return voucherService.findVoucherSaleProducts(voucherProgramId);
    }

}
