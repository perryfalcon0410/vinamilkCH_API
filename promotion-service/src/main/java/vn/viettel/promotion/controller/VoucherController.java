package vn.viettel.promotion.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.dto.voucher.VoucherSaleProductDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.messaging.VoucherFilter;
import vn.viettel.promotion.messaging.VoucherUpdateRequest;
import vn.viettel.promotion.service.VoucherService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(tags = "API voucher sử dụng cho bán hàng")
public class VoucherController extends BaseController {

    @Autowired
    VoucherService voucherService;
    private final String root = "/promotions/vouchers";

    // find vouchers for sale
    @GetMapping(value = { V1 + root})
    @ApiOperation(value = "Tìm kiếm voucher trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<VoucherDTO>> findVouchers(HttpServletRequest request,
                                       @RequestParam( name = "keyWord", required = false, defaultValue = "") String keyWord,
                                       Pageable pageable) {
        VoucherFilter voucherFilter = new VoucherFilter(keyWord);
        Response<Page<VoucherDTO>> response = voucherService.findVouchers(voucherFilter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn voucher trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<VoucherDTO> getVoucher(HttpServletRequest request, @PathVariable Long id, @RequestParam("customerTypeId") Long customerTypeId) {
        Response<VoucherDTO> response = voucherService.getVoucher(id, this.getShopId(), customerTypeId);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return response;
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/feign/{id}"})
    public Response<Voucher> getFeignVoucher(@PathVariable Long id) {
        return voucherService.getFeignVoucher(id);
    }

    @RoleFeign
    @PatchMapping(value = { V1 + root + "/{id}"})
    public Response<VoucherDTO> updateVoucher(@PathVariable Long id,
                                              @Valid @RequestBody VoucherUpdateRequest request) {
        return voucherService.updateVoucher(id, request, this.getUserId());
    }


    @RoleFeign
    @GetMapping(value = { V1 + root + "/voucher-sale-products/{voucherProgramId}"})
    public Response<List<VoucherSaleProductDTO>> findVoucherSaleProducts(@PathVariable Long voucherProgramId) {
        return voucherService.findVoucherSaleProducts(voucherProgramId);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-by-sale-order-id/{id}"})
    public Response<List<VoucherDTO>> getVoucherBySaleOrderId(@PathVariable Long id) {
        return voucherService.getVoucherBySaleOrderId(id);
    }
}
