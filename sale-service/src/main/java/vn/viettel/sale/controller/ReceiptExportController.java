package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.messaging.ReceiptExportCreateRequest;
import vn.viettel.sale.messaging.ReceiptExportUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.PoTransDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;
import vn.viettel.sale.service.dto.StockAdjustmentDTO;
import vn.viettel.sale.service.dto.StockBorrowingDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API chức năng xuất hàng")
@Slf4j
public class ReceiptExportController extends BaseController {
    @Autowired
    ReceiptExportService receiptExportService;
    @Autowired
    JMSSender jmsSender;
    
    private final String root = "/sales/export";

    @GetMapping(value = { V1 + root})
    @ApiOperation(value = "Lấy danh sách phiếu xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(
             HttpServletRequest request,
             @ApiParam("Mã phiếu xuất")@RequestParam(value = "transCode",required = false) String transCode,
             @ApiParam("Số hóa đơn, Mã giao dịch") @RequestParam(value = "redInvoiceNo",required = false) String redInvoiceNo,
             @ApiParam("Từ ngày xuất")@RequestParam(value = "fromDate") Date fromDate,
             @ApiParam("Đến ngày xuất")@RequestParam(value = "toDate") Date toDate,
             @ApiParam("Loại xuất")@RequestParam(value = "type",required = false) Integer type,
             @SortDefault.SortDefaults({
                     @SortDefault(sort = "transDate", direction = Sort.Direction.DESC),
                     @SortDefault(sort = "transCode", direction = Sort.Direction.DESC)
             })
             Pageable pageable) {
        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response = receiptExportService.find(transCode, redInvoiceNo, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate),type,this.getShopId(),pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_RECEIPT_EXPORT_SUCCESS);
        return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>().withData(response);
    }

    @PostMapping(value = { V1 + root })
    @ApiOperation(value = "Tạo phiếu xuất hàng phiếu xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> createReceipt(HttpServletRequest request, @Valid @RequestBody ReceiptExportCreateRequest rq) {
        List<Long> syncIds = receiptExportService.createReceipt(rq, this.getUserId(),this.getShopId());
        ResponseMessage message = ResponseMessage.CREATE_FAILED;
        if(syncIds != null) {
	        switch (rq.getImportType()){
	        case 0:
		        	if(validateListOneItem(syncIds)) {
		               	sendSynRequest(JMSType.po_trans, Arrays.asList(syncIds.get(0)));
		        	}
	               	message = ResponseMessage.CREATED_SUCCESSFUL;
	               	break;
	        case 1:
	        		if(validateListOneItem(syncIds)) {
		               	sendSynRequest(JMSType.stock_adjustment, Arrays.asList(syncIds.get(0)));
		        	}
	        		if(validateListTwoItem(syncIds)) {
		               	sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(syncIds.get(1)));
		        	}
	               	message = ResponseMessage.CREATED_SUCCESSFUL;
	               	break;
	        case 2:
	        		if(validateListOneItem(syncIds)) {
	        			sendSynRequest(JMSType.stock_borrowing, Arrays.asList(syncIds.get(0)));
	        		}
	        		if(validateListTwoItem(syncIds)) {
	        			sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(syncIds.get(1)));
	        		}
	        		message = ResponseMessage.CREATED_SUCCESSFUL;
	        		break;
	        }
        }
        
        Response response = new Response();
        response.setStatusValue(message.statusCodeValue());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CREATE_RECEIPT_EXPORT_SUCCESS);
        return response;
    }
    @PatchMapping(value = { V1 + root + "/update/{Id}"})
    @ApiOperation(value = "Chỉnh sửa phiếu xuất hàng phiếu xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> updateReceiptExport(HttpServletRequest request, @Valid @RequestBody ReceiptExportUpdateRequest rq,
                                                @ApiParam("Id phiếu xuất")@PathVariable long Id) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.UPDATE_RECEIPT_EXPORT_SUCCESS);
        List<Long> syncIds = receiptExportService.updateReceiptExport(rq, Id,this.getShopId());
        ResponseMessage message = ResponseMessage.UPDATE_FAILED;
        if(syncIds != null) {
	        switch (rq.getType()){
		        case 0:
		            	sendSynRequest(JMSType.po_trans, syncIds);
		            	message = ResponseMessage.UPDATE_SUCCESSFUL;
		            	break;
			    case 1:
			        	sendSynRequest(JMSType.stock_adjustment_trans, syncIds);
			        	message = ResponseMessage.UPDATE_SUCCESSFUL;
			        	break;
			    case 2:
			            sendSynRequest(JMSType.stock_borrowing_trans, syncIds);
			            message = ResponseMessage.UPDATE_SUCCESSFUL;
			            break;
	        }
        }
        Response response = new Response();
        response.setStatusValue(message.statusCodeValue());
        response.setStatusCode(message.statusCode());
        return response;
    }
    @PutMapping(value = { V1 + root + "/remove/{Id}"})
    @ApiOperation(value = "Xóa phiếu xuất hàng phiếu xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> removeReceiptExport(HttpServletRequest request,
                                                @ApiParam("Loại phiếu xuất")@RequestParam Integer type,
                                                @ApiParam("Id phiếu xuất")@PathVariable long Id) {
        List<List<String>> syncIds = receiptExportService.removeReceiptExport(type,Id,this.getShopId());
        ResponseMessage message = ResponseMessage.UPDATE_SUCCESSFUL;
        if(syncIds != null) {
        switch (type){
	        case 0:
	        		if(validateListStringOneItem(syncIds)) {
	        			sendSynRequest(JMSType.po_trans, Arrays.asList(Long.parseLong(syncIds.get(0).get(0))));
	        		}
	        		message = ResponseMessage.DELETE_SUCCESSFUL;
	        		break;
	        case 1:
	        		if(validateListStringOneItem(syncIds)) {
	        			sendSynRequest(JMSType.stock_adjustment, Arrays.asList(Long.parseLong(syncIds.get(0).get(0))));
	        		}
	        		if(validateListStringTwoItem(syncIds)) {
	        			sendSynRequest(JMSType.stock_adjustment_trans, Arrays.asList(Long.parseLong(syncIds.get(1).get(0))));
	        		}
	        		if(validateListStringThreeItem(syncIds)) {
	        			sendSynRequestByCode(JMSType.sale_orders_adjustment, syncIds.get(2));
	        		}
	            	message = ResponseMessage.DELETE_SUCCESSFUL;
	            	break;
	        case 2:
	        		if(validateListStringOneItem(syncIds)) {
	        			sendSynRequest(JMSType.stock_borrowing, Arrays.asList(Long.parseLong(syncIds.get(0).get(0))));
	        		}
	        		if(validateListStringTwoItem(syncIds)) {
	             		sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(Long.parseLong(syncIds.get(1).get(0))));
	             	}
	             	message = ResponseMessage.DELETE_SUCCESSFUL;
	             	break;
        	}
        }
        Response response = new Response();
        response.setStatusValue(message.statusCodeValue());
        response.setStatusCode(message.statusCode());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.REMOVE_RECEIPT_EXPORT_SUCCESS);
        return response;
    }


    @GetMapping(value = { V1 + root + "/po-trans"})
    @ApiOperation(value = "Danh sách phiếu nhập hàng dùng để xuất trả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<PoTransDTO>> getListPoTrans(HttpServletRequest request,
                                                     @ApiParam("Mã phiếu nhập")@RequestParam(value = "transCode",required = false) String transCode,
                                                     @ApiParam("Số hóa đơn")@RequestParam(value = "redInvoiceNo",required = false) String redInvoiceNo,
                                                     @ApiParam("Số nội bộ")@RequestParam(value = "internalNumber",required = false) String internalNumber,
                                                     @ApiParam("Số PO")@RequestParam(value = "poNo",required = false) String poNo,
                                                     @ApiParam("Từ ngày nhập")@RequestParam(value = "fromDate",required = false ) Date fromDate,
                                                     @ApiParam("Đến ngày nhập")@RequestParam(value = "toDate",required = false) Date toDate,
                                                     @SortDefault.SortDefaults({
                                                             @SortDefault(sort = "transDate", direction = Sort.Direction.DESC),
                                                             @SortDefault(sort = "transCode", direction = Sort.Direction.DESC)
                                                     })Pageable pageable) {
        Page<PoTransDTO> response = receiptExportService.getListPoTrans(transCode,redInvoiceNo,internalNumber,poNo,DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate),this.getShopId(),pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PO_TRANS_SUCCESS);
        return new Response<Page<PoTransDTO>>().withData(response);
    }
    @GetMapping(value = { V1 + root + "/adjustment"})
    @ApiOperation(value = "Danh sách phiếu xuất điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment(HttpServletRequest request,Pageable pageable) {
        List<StockAdjustmentDTO> response =receiptExportService.getListStockAdjustment(this.getShopId(), pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_STOCK_ADJUSTMENT_SUCCESS);
        return new Response<List<StockAdjustmentDTO>>().withData(response);
    }
    @GetMapping(value = { V1 + root + "/borrowing"})
    @ApiOperation(value = "Danh sách phiếu xuất vay mượn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockBorrowingDTO>> getListStockBorrowing(HttpServletRequest request,Pageable pageable) {
        List<StockBorrowingDTO> response = receiptExportService.getListStockBorrowing(this.getShopId(),pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_STOCK_BORROWING_SUCCESS);
        return new Response<List<StockBorrowingDTO>>().withData(response);
    }
    
    private void sendSynRequest(String type, List<Long> listId) {
        try {
        	if(!listId.isEmpty()) {
        		jmsSender.sendMessage(type, listId);
        	}
        } catch (Exception ex) {
            log.error("khoi tao jmsSender", ex);
        }
    }
    
    private void sendSynRequestByCode(String type, List<String> lstCodes) {
        try {
        	if(!lstCodes.isEmpty()) {
        		jmsSender.sendMessageByCode(type, lstCodes);
        	}
        } catch (Exception ex) {
            log.error("Cannot send request", ex);
        }
    }
    
    
    private boolean validateListOneItem (List<Long> lst) {
    	return lst != null && !lst.isEmpty() && lst.get(0) != null;
    }
    
    private boolean validateListTwoItem (List<Long> lst) {
    	return lst != null && !lst.isEmpty() && lst.size() == 2 && lst.get(1) != null;
    }
    
    private boolean validateListStringOneItem (List<List<String>> lst) {
    	return lst != null && !lst.isEmpty() && lst.get(0) != null && !lst.get(0).isEmpty() && lst.get(0).get(0) != null && !lst.get(0).get(0).isEmpty();
    }
    
    private boolean validateListStringTwoItem (List<List<String>> lst) {
    	return lst != null && !lst.isEmpty() && lst.size() > 1 && lst.get(1) != null && !lst.get(1).isEmpty() && lst.get(1).get(0) != null && !lst.get(1).get(0).isEmpty();
    }
    
    private boolean validateListStringThreeItem (List<List<String>> lst) {
    	return lst != null && !lst.isEmpty() && lst.size() > 2 && lst.get(2) != null && !lst.get(2).isEmpty() && lst.get(2).get(0) != null && !lst.get(2).get(0).isEmpty();
    }
}
