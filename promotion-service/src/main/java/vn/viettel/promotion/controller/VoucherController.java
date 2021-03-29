package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.promotion.service.VoucherService;
import vn.viettel.promotion.service.dto.VoucherDTO;

import java.util.List;

@RestController
@RequestMapping("/api/promotion/voucher")
public class VoucherController extends BaseController {

    @Autowired
    VoucherService voucherService;

    // find vouchers for sale
    @GetMapping("/find")
    public Response<Page<VoucherDTO>> findVouchers(@RequestParam( name = "keyWord", defaultValue = "") String keyWord,
                                                   @RequestParam("shopId") Long shopId,
                                                   @RequestParam("customerTypeId") Long customerTypeId, Pageable pageable) {
        return voucherService.findVouchers(keyWord, shopId, customerTypeId, pageable);
    }

}
