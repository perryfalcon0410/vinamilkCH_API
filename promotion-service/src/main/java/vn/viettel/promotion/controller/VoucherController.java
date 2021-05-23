package vn.viettel.promotion.controller;

import io.swagger.annotations.*;
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
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.promotion.entities.Voucher;
import vn.viettel.promotion.messaging.VoucherFilter;
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
    @ApiOperation(value = "Tìm kiếm chính xác voucher trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<VoucherDTO>> findVouchers(HttpServletRequest request,
                                       @ApiParam("Tìm kiếm theo mã, tên hoặc serial")
                                       @RequestParam( name = "keyWord", required = false, defaultValue = "") String keyWord,
                                       Pageable pageable) {
        VoucherFilter voucherFilter = new VoucherFilter(this.getShopId(), keyWord);
        Response<Page<VoucherDTO>> response = voucherService.findVouchers(voucherFilter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_VOUCHERS_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn voucher trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<VoucherDTO> getVoucher(HttpServletRequest request, @PathVariable Long id,
                            @ApiParam("Id khách hàng") @RequestParam("customerId") Long customerId,
                            @ApiParam("Id các  sản phẩm mua") @RequestParam("productIds") List<Long> productIds) {
        Response<VoucherDTO> response = voucherService.getVoucher(id, this.getShopId(), customerId, productIds);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_VOUCHER_SUCCESS);
        return response;
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/feign/{id}"})
    public Response<Voucher> getFeignVoucher(@PathVariable Long id) {
        return voucherService.getFeignVoucher(id);
    }

    @RoleFeign
    @PutMapping(value = { V1 + root})
    public Response<VoucherDTO> updateVoucher(@Valid @RequestBody VoucherDTO request) {
        return voucherService.updateVoucher(request);
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
