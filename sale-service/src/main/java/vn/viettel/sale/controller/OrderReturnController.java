package vn.viettel.sale.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.OrderReturnRequest;
import vn.viettel.sale.messaging.SaleOrderChosenFilter;
import vn.viettel.sale.messaging.SaleOrderFilter;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.messaging.TotalOrderChoose;
import vn.viettel.sale.service.OrderReturnService;
import vn.viettel.sale.service.dto.OrderReturnDTO;
import vn.viettel.sale.service.dto.OrderReturnDetailDTO;
import vn.viettel.sale.service.dto.SaleOrderDTO;

@RestController
@Api(tags = "API sử dụng cho quản lý hóa đơn trả lại")
public class OrderReturnController extends BaseController {
    @Autowired
    OrderReturnService orderReturnService;
    
    @Autowired
    JMSSender jmsSender;
    private final String root = "/sales/order-return";

    public void setService(OrderReturnService service){
        if(orderReturnService == null) orderReturnService = service;
    }

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Danh sách hóa đơn trả lại, tìm kiếm trong danh sách")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse>> getAllOrderReturn(HttpServletRequest httpRequest, @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                                     @RequestParam(value = "customerPhone", required = false) String customerPhone,
                                                                                                     @RequestParam(value = "returnNumber", required = false) String returnNumber,
                                                                                                     @RequestParam(value = "fromDate") Date fromDate,
                                                                                                     @RequestParam(value = "toDate") Date toDate,
                                                                                                     @SortDefault.SortDefaults({
                                                                                                             @SortDefault(sort = "orderDate", direction = Sort.Direction.DESC),
                                                                                                             @SortDefault(sort = "orderNumber", direction = Sort.Direction.DESC)
                                                                                                     })
                                                                                                     Pageable pageable) {
        SaleOrderFilter filter = new SaleOrderFilter(searchKeywords, customerPhone, returnNumber, null, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
        Response<CoverResponse<Page<OrderReturnDTO>, SaleOrderTotalResponse>> response = new Response<>();
        return response.withData(orderReturnService.getAllOrderReturn(filter, pageable, this.getShopId(httpRequest)));
    }

    @GetMapping(value = { V1 + root + "/detail/{id}"})
    @ApiOperation(value = "Chi tiết hóa đơn trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<OrderReturnDetailDTO> getOrderReturnDetail(@PathVariable Long id) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        return response.withData(orderReturnService.getOrderReturnDetail(id));
    }

    @GetMapping(value = { V1 + root + "/choose"})
    @ApiOperation(value = "Danh sách chọn hóa đơn để trả, tìm kiếm trong danh sách")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<CoverResponse<List<SaleOrderDTO>,TotalOrderChoose>> selectForReturn(HttpServletRequest httpRequest, @RequestParam(value = "orderNumber", required = false) String orderNumber,
                                                                                        @RequestParam(value = "searchKeywords", required = false) String searchKeywords,
                                                                                        @RequestParam(value = "product", required = false) String product,
                                                                                        @RequestParam(value = "fromDate", required = false) Date fromDate,
                                                                                        @RequestParam(value = "toDate", required = false) Date toDate) {
        SaleOrderChosenFilter filter = new SaleOrderChosenFilter(orderNumber, searchKeywords, product, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate));
        Response<CoverResponse<List<SaleOrderDTO>,TotalOrderChoose>> response = new Response<>();
        return response.withData(orderReturnService.getSaleOrderForReturn(filter, this.getShopId(httpRequest)));
    }

    @GetMapping(value = { V1 + root + "/chosen/{id}"})
    @ApiOperation(value = "Hóa đơn đã chọn để trả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<OrderReturnDetailDTO> orderSelected(HttpServletRequest httpRequest, @PathVariable Long id) {
        Response<OrderReturnDetailDTO> response = new Response<>();
        return response.withData(orderReturnService.getSaleOrderChosen(id, this.getShopId(httpRequest)));
    }

    @PostMapping(value = { V1 + root})
    @ApiOperation(value = "Tạo hóa đơn trả lại")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response<SaleOrder> createOrderReturn(HttpServletRequest httpRequest,@Valid @RequestBody OrderReturnRequest request) {
        Response<SaleOrder> response = new Response<>();
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.CREATE_ORDER_RETURN_SUCCESS);
        response.setStatusValue("Tạo hóa đơn trả thành công");
        HashMap<String,Object> mapResult = orderReturnService.createOrderReturn(request, this.getShopId(httpRequest), this.getUsername(httpRequest));
        SaleOrder newOrderReturn = (SaleOrder) mapResult.get(JMSType.sale_order);
        if(newOrderReturn != null && newOrderReturn.getId() != null) {
        	sendSynRequest(JMSType.sale_order, Arrays.asList(newOrderReturn.getId()));
        }
        Long memberCustomerId = (Long) mapResult.get(JMSType.member_customer);
        if(memberCustomerId != null) {
        	sendSynRequest(JMSType.member_customer, Arrays.asList(memberCustomerId));
        }
        sendSynRequest(JMSType.promotion_shop_map, (List<Long>) mapResult.get(JMSType.promotion_shop_map));
        return response.withData(newOrderReturn);
    }
    
    
	private void sendSynRequest(String type, List<Long> lstIds) {
		try {
			if(!lstIds.isEmpty()) {
				jmsSender.sendMessage(type, lstIds);
			}
		} catch (Exception ex) {
			LogFile.logToFile("vn.viettel.sale.service.impl.OrderReturnImpl.sendSynRequest", type, LogLevel.ERROR, null, "has error when encode data " + ex.getMessage());
		}
	}
}
