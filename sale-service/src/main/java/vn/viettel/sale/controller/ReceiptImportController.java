package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.jms.JMSSender;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.core.utils.JMSType;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.excel.ExportExcel;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.impl.ReceiptImportServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "API chức năng nhập hàng")
@Slf4j
public class ReceiptImportController extends BaseController {
    @Autowired
    ReceiptImportService receiptService;
    @Autowired
    ShopClient shopClient;
    @Autowired
    private JMSSender jmsSender;
    private final String root = "/sales/import";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Lấy danh sách phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(
                                HttpServletRequest request,
                                @ApiParam("Mã phiếu nhập")@RequestParam(value = "transCode",required = false) String transCode,
                                @ApiParam("Số hóa đơn") @RequestParam(value ="redInvoiceNo", required = false ) String redInvoiceNo,
                                @ApiParam("Từ ngày nhập")@RequestParam(value ="fromDate",required = false) Date fromDate,
                                @ApiParam("Đến ngày nhập")@RequestParam(value ="toDate",required = false) Date toDate,
                                @ApiParam("Loại nhập")@RequestParam(value ="type", required = false ) Integer type, Pageable pageable) {
        CoverResponse<Page<ReceiptImportListDTO>, TotalResponse> response = receiptService.find(transCode, redInvoiceNo, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate),type,this.getShopId(),pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_RECEIPT_IMPORT_SUCCESS);
        return new Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>>().withData(response);
    }

    @PostMapping(value = { V1 + root })
    @ApiOperation(value = "Tạo phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> createReceipt(HttpServletRequest request,
                                            @Valid @RequestBody ReceiptCreateRequest rq) {
        List<Long> syncIds = receiptService.createReceipt(rq,this.getUserId(),this.getShopId());
        ResponseMessage message = ResponseMessage.CREATE_FAILED;
        if(syncIds != null) {
	        switch (rq.getImportType()) {
		        case 0:
		        		if(validateListOneItem(syncIds)) {
		        			sendSynRequest(JMSType.po_trans, Arrays.asList(syncIds.get(0)));
		        		}
			            if(validateListTwoItem(syncIds)) {
			            	sendSynRequest(JMSType.po_confirm, Arrays.asList(syncIds.get(1)));
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
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CREATE_RECEIPT_IMPORT_SUCCESS);
        return response;
    }
    @GetMapping(value = { V1 + root + "/trans/{id}"})
    @ApiOperation(value = "Lấy thông tin phiếu nhập hàng dùng để chỉnh sửa hoặc xem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> getTrans(HttpServletRequest request,
                                     @ApiParam("Id đơn nhập hàng")@PathVariable(name = "id") Long id,
                                     @ApiParam("Loại đơn nhập hàng")@RequestParam Integer type) {
        Object response = receiptService.getForUpdate(type,id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_ONE_RECEIPT_IMPORT_SUCCESS);
        return new Response<>().withData(response);
    }


    @PatchMapping(value = { V1 + root + "/update/{id}"})
    @ApiOperation(value = "Chỉnh sửa phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> updateReceiptImport(HttpServletRequest request,
                                                @ApiParam("Id đơn nhập hàng")@PathVariable long id,
                                                @Valid @RequestBody ReceiptUpdateRequest rq) {
        List<Long> syncIds = receiptService.updateReceiptImport(rq, id,this.getUserName(),this.getShopId());
        ResponseMessage message = ResponseMessage.UPDATE_FAILED;
        if(syncIds != null) {
	        switch (rq.getType()) {
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
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.UPDATE_RECEIPT_IMPORT_SUCCESS);
        return response;
    }

    @PatchMapping(value = { V1 + root + "/remove/{id}"})
    @ApiOperation(value = "Xóa phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> removeReceiptImport(HttpServletRequest request,
                                    @ApiParam("Id đơn nhập hàng")@PathVariable long id,
                                    @ApiParam("Loại phiếu nhập")@RequestParam Integer type ) {
        List<List<String>> syncIds = receiptService.removeReceiptImport( id,type,this.getUserName(),this.getShopId());
         ResponseMessage message = ResponseMessage.DELETE_FAILED;
         if(syncIds != null) {
		    switch (type) {
		        case 0:
		        		if(validateListStringOneItem(syncIds)) {
		        			sendSynRequest(JMSType.po_trans, Arrays.asList(Long.parseLong(syncIds.get(0).get(0))));
		        		}
		        		if(validateListStringTwoItem(syncIds)) {
		        			sendSynRequest(JMSType.po_confirm, Arrays.asList(Long.parseLong(syncIds.get(1).get(0))));
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
		        			sendSynRequest(JMSType.stock_borrowing_trans, Arrays.asList(Long.parseLong(syncIds.get(0).get(0))));
		        		}
		        		if(validateListStringTwoItem(syncIds)) {
		        			sendSynRequest(JMSType.stock_borrowing, Arrays.asList(Long.parseLong(syncIds.get(1).get(0))));
		        		}
		        		message = ResponseMessage.DELETE_SUCCESSFUL;
		        		break;
		    }
         }
        Response response = new Response();
        response.setStatusValue(message.statusCodeValue());
        response.setStatusCode(message.statusCode());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.REMOVE_RECEIPT_IMPORT_SUCCESS);
        return response;
    }


    @GetMapping(value = { V1 + root + "/po-confirm"})
    @ApiOperation(value = "Lấy danh sách phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<PoConfirmDTO>> getListPoConfirm(HttpServletRequest request) {
        List<PoConfirmDTO> response = receiptService.getListPoConfirm(this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PO_CONFIRM_SUCCESS);
        List<Long> syncIds = new ArrayList<Long>();
        if(!response.isEmpty()) {
        	syncIds = response.stream().map(po -> po.getId()).collect(Collectors.toList());
        }
        sendSynRequest(JMSType.po_confirm, syncIds);
        return new Response<List<PoConfirmDTO>>().withData(response);
    }

    @GetMapping(value = { V1 + root + "/adjustment"})
    @ApiOperation(value = "Lấy danh sách phiếu nhập điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment(HttpServletRequest request, Pageable pageable) {
        List<StockAdjustmentDTO> response = receiptService.getListStockAdjustment(this.getShopId(), pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_STOCK_ADJUSTMENT_SUCCESS);
        return new Response<List<StockAdjustmentDTO>>().withData(response);
    }
    @GetMapping(value = { V1 + root + "/borrowing"})
    @ApiOperation(value = "Lấy danh sách phiếu vay mượn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockBorrowingDTO>> getListStockBorrowing(HttpServletRequest request,Pageable pageable) {
        List<StockBorrowingDTO> response = receiptService.getListStockBorrowing(this.getShopId(),pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_STOCK_BORROWING_SUCCESS);
        return new Response<List<StockBorrowingDTO>>().withData(response);
    }
    @GetMapping(value = { V1 + root + "/po-detail0/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm bán của phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<PoDetailDTO>, TotalResponseV1>> getPoDetailByPoId(HttpServletRequest request,
                                                                                         @ApiParam("Id đơn mua hàng")@PathVariable Long id) {
        CoverResponse<List<PoDetailDTO>,TotalResponseV1> response = receiptService.getPoDetailByPoId(id,this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PRODUCT_FOR_SALE_OF_PO_CONFIRM_SUCCESS);
        return new Response<CoverResponse<List<PoDetailDTO>,TotalResponseV1>>().withData(response);
    }


    @GetMapping(value = { V1 + root + "/po-detail1/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm khuyến mãi của phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<PoDetailDTO>,TotalResponseV1>> getPoDetailByPoIdAndPriceIsNull(
                                                HttpServletRequest request,
                                                @ApiParam("Id đơn mua hàng")@PathVariable Long id) {
        CoverResponse<List<PoDetailDTO>,TotalResponseV1> response = receiptService.getPoDetailByPoIdAndPriceIsNull(id,this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PRODUCT_PROMOTION_OF_PO_CONFIRM_SUCCESS);
        return new Response<CoverResponse<List<PoDetailDTO>,TotalResponseV1>>().withData(response);
    }


    @GetMapping(value = { V1 + root + "/adjustment-detail/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm của phiếu điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse>> getStockAdjustmentDetail(
                                HttpServletRequest request,
                                @ApiParam("Id phiếu điều chỉnh")@PathVariable Long id) {
        CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse> response = receiptService.getStockAdjustmentDetail(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_ADJUSTMENT_DETAIL_SUCCESS);
        return new Response<CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse>>().withData(response);
    }


    @GetMapping(value = { V1 + root + "/borrowing-detail/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm của phiếu vay mượn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse>> getStockBorrowingDetail(
                                                    HttpServletRequest request,
                                                    @ApiParam("Id phiếu vay mượn")@PathVariable Long id) {
        CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse> response = receiptService.getStockBorrowingDetail(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_BORROWING_DETAIL_SUCCESS);
        return new Response<CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse>>().withData(response);
    }
    @GetMapping(value = { V1 + root + "/trans-detail/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm của phiếu giao dịch nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> getPoTransDetail(HttpServletRequest request,
                                             @ApiParam("Id phiếu nhập hàng")@PathVariable Long id,
                                             @ApiParam("Loại phiếu nhập")@RequestParam Integer type) {
        Object response = receiptService.getTransDetail(type,id,this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_RECEIPT_IMPORT_DETAIL_SUCCESS);
        return new Response<>().withData(response);
    }

    @GetMapping(V1 + root +"/warehouse-type")
    @ApiOperation(value = "Lấy kho mặc định của cửa hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<WareHouseTypeDTO>  getWareHouseType(HttpServletRequest request) {
        WareHouseTypeDTO response = receiptService.getWareHouseTypeName(this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_DEFAULT_WARE_HOUSE_SUCCESS);
        return new Response<WareHouseTypeDTO>().withData(response);
    }

    @PutMapping(value = { V1 + root + "/not-import/{Id}"})
    @ApiOperation(value = "Xét phiếu mua hàng thành không nhập")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<ResponseMessage> setNotImport(HttpServletRequest request,
                                @ApiParam("Id phiếu mua hàng")@PathVariable long Id,
                                @RequestBody NotImportRequest rq) {
        ResponseMessage message = receiptService.setNotImport(Id,this.getUserName(),rq);
        Response response = new Response();
        response.setStatusValue(message.statusCodeValue());
        response.setStatusCode(message.statusCode());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.SET_PO_CONFIRM_NOT_IMPORT_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/excel/{poId}"})
    @ApiOperation(value = "Xuất excel phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public void exportToExcel(
                    @ApiParam("Id phiếu mua hàng")@PathVariable Long poId, HttpServletResponse response) throws IOException {

        CoverResponse<List<PoDetailDTO>,TotalResponseV1> soConfirmList = receiptService.getPoDetailByPoId(poId,this.getShopId());
        List<PoDetailDTO> list1 = soConfirmList.getResponse();
        CoverResponse<List<PoDetailDTO>,TotalResponseV1> soConfirmList2 = receiptService.getPoDetailByPoIdAndPriceIsNull(poId,this.getShopId());
        List<PoDetailDTO> list2 = soConfirmList2.getResponse();
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        ShopDTO shops = shopClient.getByIdV1(shop.getParentShopId()).getData();
        ExportExcel exportExcel = new ExportExcel(list1,list2,shops);
        ByteArrayInputStream in = exportExcel.export();

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=Phieu_mua_hang_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        in.close();
        response.getOutputStream().flush();
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
