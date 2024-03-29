package vn.viettel.promotion.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.voucher.VoucherDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.promotion.service.VoucherService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "API voucher sử dụng cho bán hàng")
public class VoucherController extends BaseController {

    @Autowired
    VoucherService voucherService;
    private final String root = "/promotions/vouchers";

    @PostMapping(value = { V1 + root + "/code"})
    @ApiOperation(value = "Tìm voucher theo serial trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<VoucherDTO> getVoucherBySerial(HttpServletRequest request, @RequestBody Map<String, String> voucher,
                                                 @ApiParam("Id khách hàng") @RequestParam("customerId") Long customerId,
                                                 @ApiParam("Id các  sản phẩm mua") @RequestParam("productIds") List<Long> productIds) {
        VoucherDTO response = voucherService.getVoucherBySerial(voucher.get("serial"), this.getShopId(request), customerId, productIds);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_VOUCHER_SUCCESS);
        return new Response<VoucherDTO>().withData(response);
    }

    @PutMapping(value = { V1 + root})
    @ApiOperation(value = "Cập nhật voucher")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<VoucherDTO> updateVoucher(@Valid @RequestBody VoucherDTO request) {
        VoucherDTO dto = voucherService.updateVoucher(request);
        return new Response<VoucherDTO>().withData(dto);
    }

    @ApiOperation(value = "Lấy voucher theo id trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/feign/{id}"})
    public Response<VoucherDTO> getFeignVoucher(@ApiParam("Id voucher") @PathVariable Long id) {
        VoucherDTO voucher = voucherService.getFeignVoucher(id);
        return new Response<VoucherDTO>().withData(voucher);
    }


    @ApiOperation(value = "Lấy danh sách voucher bởi id của sale order")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/get-by-sale-order-id/{id}"})
    public Response<List<VoucherDTO>> getVoucherBySaleOrderId(@PathVariable Long id) {
        List<VoucherDTO> list = voucherService.getVoucherBySaleOrderId(id);
        return new Response< List<VoucherDTO>>().withData(list);
    }


    @ApiOperation(value = "Update voucher cho đơn trả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PutMapping(value = { V1 + root + "/return"})
    public Response<Boolean> returnVoucher(@RequestParam Long saleOrderId) {
       Boolean response = voucherService.returnVoucher(saleOrderId);
        return new Response<Boolean>().withData(response);
    }

    public void setService(VoucherService service) {
        if(voucherService == null) voucherService = service;
    }
}
