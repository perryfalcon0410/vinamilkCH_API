package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng cho quản lý đổi hàng hỏng")
public class ExchangeTransController extends BaseController {
    @Autowired
    ExchangeTranService service;
    private final String root = "/sales/exchangetrans";

    @ApiOperation(value = "Api dùng khi tạo mới đơn đổi hàng để lấy lý do trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 9021, message = "Lý do đổi trả không hợp lệ")
    })
    @GetMapping(value = { V1 + root + "/reasons"})
    public Response<List<CategoryDataDTO>> getAllReason(HttpServletRequest request) {
        Response<List<CategoryDataDTO>> response = service.getReasons();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_EXCHANGE_REASON_SUCCESS);

        return response;
    }

    @ApiOperation(value = "Api dùng để lấy danh sách đơn đổi hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root})
    public Response<CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO>> getAllExchangeTrans(@RequestParam(required = false) String transCode, HttpServletRequest request,
                                                                                                 @RequestParam(required = false) Date fromDate,
                                                                                                 @RequestParam(required = false) Date toDate, @RequestParam(required = false) Long reasonId, Pageable pageable) {
        CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> response =
                service.getAllExchange(this.getRoleId(), this.getShopId(), transCode, fromDate, toDate, reasonId, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_EXCHANGE_LIST_SUCCESS);

        return new Response<CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để tạo mới đơn đổi hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })

    @PostMapping(value = { V1 + root + "/create"})
    public Response<ExchangeTrans> create(@Valid @RequestBody ExchangeTransRequest request, HttpServletRequest httpRequest) {
        ExchangeTrans response = service.create(request, this.getUserId(),this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.CREATE_EXCHANGE_TRANS_SUCCESS);

        return new Response<ExchangeTrans>().withData(response);
    }

    @ApiOperation(value = "Api lấy 1 đơn đổi trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ExchangeTransDTO> getExchangeTrans(@PathVariable Long id, HttpServletRequest httpRequest) {
        ExchangeTransDTO response = service.getExchangeTrans(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_EXCHANGE_TRANS_BY_ID_SUCCESS);

        return new Response<ExchangeTransDTO>().withData(response);
    }

    @ApiOperation(value = "Api chỉnh sửa đơn đổi trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })
    @PutMapping(value = { V1 + root + "/update/{id}"})
    public Response<String> update(@PathVariable Long id,@RequestBody  ExchangeTransRequest request, HttpServletRequest httpRequest) {
        String response = service.update(id,request,this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.UPDATE_EXCHANGE_TRANS_SUCCESS);

        return new Response<String>().withData(response);
    }

    @ApiOperation(value = "Api dùng để lấy danh sách hàng hỏng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
    })
    @GetMapping(V1 + root + "/products/{id}")
    public Response<List<ExchangeTransDetailRequest>> getBrokenProducts(@PathVariable Long id, HttpServletRequest httpRequest) {
        List<ExchangeTransDetailRequest> response = service.getBrokenProducts(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.GET_BROKEN_PRODUCT_SUCCESS);

        return new Response<List<ExchangeTransDetailRequest>>().withData(response);
    }
}
