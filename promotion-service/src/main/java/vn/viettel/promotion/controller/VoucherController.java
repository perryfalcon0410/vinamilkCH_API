package vn.viettel.promotion.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(value = { V1 + root + "/code/{serial}"})
    @ApiOperation(value = "Tìm voucher theo mã trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<VoucherDTO> getVoucherByCode(HttpServletRequest request, @PathVariable String serial,
                                           @ApiParam("Id khách hàng") @RequestParam("customerId") Long customerId,
                                           @ApiParam("Id các  sản phẩm mua") @RequestParam("productIds") List<Long> productIds) {
        VoucherDTO response = voucherService.getVoucherByCode(serial, this.getShopId(), customerId, productIds);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_VOUCHER_SUCCESS);
        return new Response<VoucherDTO>().withData(response);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/feign/{id}"})
    public Response<VoucherDTO> getFeignVoucher(@PathVariable Long id) {
        VoucherDTO voucher = voucherService.getFeignVoucher(id);
        return new Response<VoucherDTO>().withData(voucher);
    }

    @RoleFeign
    @PutMapping(value = { V1 + root})
    public Response<VoucherDTO> updateVoucher(@Valid @RequestBody VoucherDTO request) {
        VoucherDTO dto = voucherService.updateVoucher(request);
        return new Response<VoucherDTO>().withData(dto);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/voucher-sale-products/{voucherProgramId}"})
    public Response<List<VoucherSaleProductDTO>> findVoucherSaleProducts(@PathVariable Long voucherProgramId) {
        List<VoucherSaleProductDTO> list = voucherService.findVoucherSaleProducts(voucherProgramId);
        return new Response<List<VoucherSaleProductDTO>>().withData(list);
    }

    @RoleFeign
    @GetMapping(value = { V1 + root + "/get-by-sale-order-id/{id}"})
    public Response<List<VoucherDTO>> getVoucherBySaleOrderId(@PathVariable Long id) {
        List<VoucherDTO> list = voucherService.getVoucherBySaleOrderId(id);
        return new Response< List<VoucherDTO>>().withData(list);
    }
}
