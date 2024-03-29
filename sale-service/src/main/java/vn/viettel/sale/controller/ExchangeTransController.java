package vn.viettel.sale.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.ExchangeTranService;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

@RestController
@Api(tags = "Api sử dụng cho quản lý đổi hàng hỏng")
@Slf4j
public class ExchangeTransController extends BaseController {
    @Autowired
    ExchangeTranService service;
    @Autowired
    private JMSSender jmsSender;
    private final String root = "/sales/exchangetrans";

    public void setService(ExchangeTranService service){
        if(this.service == null) this.service = service;
    }

    @ApiOperation(value = "Api dùng khi tạo mới đơn đổi hàng để lấy lý do trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 9021, message = "Lý do đổi trả không hợp lệ")
    })
    @GetMapping(value = { V1 + root + "/reasons"})
    public Response<List<CategoryDataDTO>> getAllReason(HttpServletRequest request) {
        Response<List<CategoryDataDTO>> response = service.getReasons();
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_EXCHANGE_REASON_SUCCESS);

        return response;
    }

    @ApiOperation(value = "Api dùng để lấy danh sách đơn đổi hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root})
    public Response<CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO>> getAllExchangeTrans(@RequestParam(value = "transCode", required = false) String transCode, HttpServletRequest request,
                                                                                                 @RequestParam(value = "fromDate") Date fromDate,
                                                                                                 @RequestParam(value = "toDate") Date toDate,
                                                                                                 @RequestParam(value = "reasonId", required = false) Long reasonId,
                                                                                                 @SortDefault.SortDefaults({
                                                                                                         @SortDefault(sort = "transDate", direction = Sort.Direction.DESC)
                                                                                                 }) Pageable pageable) {
        CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> response =
                service.getAllExchange(this.getShopId(request), transCode, fromDate, toDate, reasonId, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_EXCHANGE_LIST_SUCCESS);

        return new Response<CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để tạo mới đơn đổi hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Khách hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })

    @PostMapping(value = { V1 + root + "/create"})
    public Response<String> create(@Valid @RequestBody ExchangeTransRequest request, HttpServletRequest httpRequest) {
        ExchangeTransDTO dto = service.create(request, this.getUserId(httpRequest),this.getShopId(httpRequest));
    	if(dto != null && dto.getId() != null) {
    		sendSynRequest(JMSType.exchange_trans, Arrays.asList(dto.getId()));
    	}
        Response response = new Response();
        response.setStatusValue(ResponseMessage.CREATED_SUCCESSFUL.statusCodeValue());
        response.setStatusCode(ResponseMessage.CREATED_SUCCESSFUL.statusCode());
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.CREATE_EXCHANGE_TRANS_SUCCESS);
        return response;
    }

    @ApiOperation(value = "Api lấy 1 đơn đổi trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Đơn trả hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ExchangeTransDTO> getExchangeTrans(@PathVariable Long id, HttpServletRequest httpRequest) {
        ExchangeTransDTO response = service.getExchangeTrans(id);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_EXCHANGE_TRANS_BY_ID_SUCCESS);

        return new Response<ExchangeTransDTO>().withData(response);
    }

    @ApiOperation(value = "Api chỉnh sửa đơn đổi trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message = "Đơn trả hàng không tìm thấy"),
            @ApiResponse(code = 9023, message = "Không tìm thấy id lý do")
    })
    @PutMapping(value = { V1 + root + "/update/{id}"})
    public Response<String> update(@PathVariable Long id,@RequestBody @Valid ExchangeTransRequest request, HttpServletRequest httpRequest) {
        ExchangeTransDTO dto  = service.update(id,request,this.getShopId(httpRequest));
    	if(dto != null && dto.getId() != null) {
    		sendSynRequest(JMSType.exchange_trans, Arrays.asList(dto.getId()));
    	}
        Response response = new Response();
        response.setStatusValue(ResponseMessage.UPDATE_SUCCESSFUL.statusCodeValue());
        response.setStatusCode(ResponseMessage.UPDATE_SUCCESSFUL.statusCode());
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.UPDATE_EXCHANGE_TRANS_SUCCESS);
        return response;
    }

    @ApiOperation(value = "Api chỉnh sửa đơn đổi trả hàng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 7008, message =  "Không tìm thấy đơn trả"),
    })
    @PutMapping(value = { V1 + root + "/remove/{id}"})
    public Response<ResponseMessage> remove(@PathVariable Long id, HttpServletRequest httpRequest) {
        ExchangeTrans exchangeTrans = service.remove(id,this.getShopId(httpRequest));
    	if(exchangeTrans != null && exchangeTrans.getId() != null) {
    		sendSynRequest(JMSType.exchange_trans, Arrays.asList(exchangeTrans.getId()));
    	}
        Response response = new Response();
        response.setStatusValue(ResponseMessage.DELETE_SUCCESSFUL.statusCodeValue());
        response.setStatusCode(ResponseMessage.DELETE_SUCCESSFUL.statusCode());
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.UPDATE_EXCHANGE_TRANS_SUCCESS);

        return response ;
    }

    @ApiOperation(value = "Api dùng để lấy danh sách hàng hỏng")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
    })
    @GetMapping(V1 + root + "/products/{id}")
    public Response<List<ExchangeTransDetailRequest>> getBrokenProducts(@PathVariable Long id, HttpServletRequest httpRequest) {
        List<ExchangeTransDetailRequest> response = service.getBrokenProducts(id);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.GET_BROKEN_PRODUCT_SUCCESS);

        return new Response<List<ExchangeTransDetailRequest>>().withData(response);
    }
    
    private void sendSynRequest(String type, List<Long> listId) {
        try {
            jmsSender.sendMessage(type, listId);
        } catch (Exception ex) {
            log.error("khoi tao jmsSender", ex);
        }
    }

}
