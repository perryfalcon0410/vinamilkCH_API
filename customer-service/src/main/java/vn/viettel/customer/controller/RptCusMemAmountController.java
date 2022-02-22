package vn.viettel.customer.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.customer.RptCusMemAmountDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
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
    @GetMapping(value = {V1 + root + "/customer-id/{id}"})
    public Response<RptCusMemAmountDTO> FindByCustomerId(HttpServletRequest httpRequest, @PathVariable Long id) {
        RptCusMemAmountDTO rptCusMemAmountDTO = rptCusMemAmountService.findByCustomerId(id);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.FIND_RPT_CUS_MEM_AMOUNT_SUCCESS);
        return new Response<RptCusMemAmountDTO>().withData(rptCusMemAmountDTO);
    }

    public void setService(RptCusMemAmountService service) {
        if(rptCusMemAmountService == null) rptCusMemAmountService = service;
    }
}
