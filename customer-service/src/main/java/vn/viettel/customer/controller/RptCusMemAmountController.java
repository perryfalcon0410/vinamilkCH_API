package vn.viettel.customer.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.RptCusMemAmountRequest;
import vn.viettel.core.security.anotation.RoleFeign;
import vn.viettel.customer.service.RptCusMemAmountService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RptCusMemAmountController extends BaseController {
    @Autowired
    private RptCusMemAmountService rptCusMemAmountService;

    private final String root = "/customers/prt-cus-mem-amounts";

    @ApiOperation(value = "Tìm kiếm tổng hợp doanh số của khách hàng bằng customerId")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root + "/customer-id/{id}"})
    public Response<RptCusMemAmountDTO> FindByCustomerId(HttpServletRequest httpRequest, @PathVariable Long id) {
        RptCusMemAmountDTO rptCusMemAmountDTO = rptCusMemAmountService.findByCustomerId(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.FIND_RPT_CUS_MEM_AMOUNT_SUCCESS);
        return new Response<RptCusMemAmountDTO>().withData(rptCusMemAmountDTO);
    }

    @RoleFeign
    @ApiOperation(value = "Cập nhật thông tin trong bán hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @PutMapping(value = { V1 + root +"/{id}" })
    public Response<Boolean> updateRptCus(@PathVariable Long id, @RequestBody RptCusMemAmountRequest request) {
        Boolean result = rptCusMemAmountService.updateRptCus(id, request);
        return new Response<Boolean>().withData(result);
    }

    @RoleFeign
    @ApiOperation(value = "Tìm kiếm tổng hợp doanh số của khách hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = { V1 + root +"/feign/customer/{customerId}" })
    public Response<RptCusMemAmountDTO> updateRptCus(@PathVariable Long customerId, @RequestParam Long shopId) {
        RptCusMemAmountDTO result = rptCusMemAmountService.getRptCus(customerId, shopId);
        return new Response<RptCusMemAmountDTO>().withData(result);
    }



}
