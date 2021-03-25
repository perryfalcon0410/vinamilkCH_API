package vn.viettel.promotion.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/find")
    public Response<List<VoucherDTO>> findVouchers( @RequestParam("keyWord") String keyWord, @RequestParam("shopId") Long shopId,
        @RequestParam("customerTypeId") Long customerTypeId) {
        return voucherService.findVouchers(keyWord, shopId, customerTypeId);
    }

}
