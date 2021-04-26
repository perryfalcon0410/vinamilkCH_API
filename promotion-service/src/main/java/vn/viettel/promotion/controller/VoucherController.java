package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.messaging.VoucherFilter;
import vn.viettel.promotion.messaging.VoucherUpdateRequest;
import vn.viettel.promotion.service.VoucherService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class VoucherController extends BaseController {

    @Autowired
    VoucherService voucherService;
    private final String root = "/promotions/vouchers";

    // find vouchers for sale
    @RoleAdmin
    @GetMapping(value = { V1 + root})
    public Response<Page<VoucherDTO>> findVouchers(@RequestParam( name = "keyWord", defaultValue = "") String keyWord,
                                                   Pageable pageable) {
        VoucherFilter voucherFilter = new VoucherFilter(keyWord);
        return voucherService.findVouchers(voucherFilter, pageable);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<VoucherDTO> getVoucher(@PathVariable Long id, @RequestParam("customerTypeId") Long customerTypeId) {
        return voucherService.getVoucher(id, this.getShopId(), customerTypeId);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/feign/{id}"})
    public Response<Voucher> getFeignVoucher(@PathVariable Long id) {
        return voucherService.getFeignVoucher(id);
    }

    @RoleAdmin
    @PatchMapping(value = { V1 + root + "/{id}"})
    public Response<VoucherDTO> updateVoucher(@PathVariable Long id,
                                              @Valid @RequestBody VoucherUpdateRequest request) {
        return voucherService.updateVoucher(id, request, this.getUserId());
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/voucher-sale-products/{voucherProgramId}"})
    public Response<List<VoucherSaleProductDTO>> findVoucherSaleProducts(@PathVariable Long voucherProgramId) {
        return voucherService.findVoucherSaleProducts(voucherProgramId);
    }

    @RoleAdmin
    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-by-sale-order-id/{id}"})
    public Response<List<VoucherDTO>> getVoucherBySaleOrderId(@PathVariable Long id) {
        return voucherService.getVoucherBySaleOrderId(id);
    }
}
