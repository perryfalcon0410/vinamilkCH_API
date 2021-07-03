package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API chức năng xuất hàng")
public class ReceiptExportController extends BaseController {
    @Autowired
    ReceiptExportService receiptExportService;

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
             @ApiParam("Từ ngày xuất")@RequestParam(value = "fromDate",required = false) Date fromDate,
             @ApiParam("Đến ngày xuất")@RequestParam(value = "toDate",required = false) Date toDate,
             @ApiParam("Loại xuất")@RequestParam(value = "type",required = false) Integer type,
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
        ResponseMessage message = receiptExportService.createReceipt(rq, this.getUserId(),this.getShopId());
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
    public Response<String> updateReceiptExport(HttpServletRequest request,@RequestBody ReceiptExportUpdateRequest rq,
                                                @ApiParam("Id phiếu xuất")@PathVariable long Id) {
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.UPDATE_RECEIPT_EXPORT_SUCCESS);
        ResponseMessage message = receiptExportService.updateReceiptExport(rq, Id,this.getShopId());
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
        ResponseMessage message = receiptExportService.removeReceiptExport(type,Id,this.getShopId());
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
                                                     @ApiParam("Đến ngày nhập")@RequestParam(value = "toDate",required = false) Date toDate, Pageable pageable) {
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
}
