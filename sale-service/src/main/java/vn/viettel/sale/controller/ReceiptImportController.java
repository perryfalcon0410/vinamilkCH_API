package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.messaging.NotImportRequest;
import vn.viettel.sale.messaging.ReceiptCreateRequest;
import vn.viettel.sale.messaging.ReceiptUpdateRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.excel.ExportExcel;
import vn.viettel.sale.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "API chức năng nhập hàng")
public class ReceiptImportController extends BaseController {
    @Autowired
    ReceiptImportService receiptService;
    @Autowired
    ShopClient shopClient;
        private final String root = "/sales/import";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Lấy danh sách phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(
                                HttpServletRequest request,
                                @ApiParam("Số hóa đơn") @RequestParam(value ="redInvoiceNo", required = false ) String redInvoiceNo,
                                @ApiParam("Từ ngày nhập")@RequestParam(value ="fromDate",required = false) Date fromDate,
                                @ApiParam("Đến ngày nhập")@RequestParam(value ="toDate",required = false) Date toDate,
                                @ApiParam("Loại nhập")@RequestParam(value ="type", required = false ) Integer type, Pageable pageable) {
        Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> response = receiptService.find(redInvoiceNo,fromDate,toDate,type,this.getShopId(),pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_RECEIPT_IMPORT_SUCCESS);
        return response;
    }

    @PostMapping(value = { V1 + root })
    @ApiOperation(value = "Tạo phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> createReceipt(HttpServletRequest request,
                                            @Valid @RequestBody ReceiptCreateRequest rq) {
        Response<Object> response = receiptService.createReceipt(rq,this.getUserId(),this.getShopId());
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
        Response<Object> response = receiptService.getForUpdate(type,id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_ONE_RECEIPT_IMPORT_SUCCESS);
        return response;
    }


    @PatchMapping(value = { V1 + root + "/update/{id}"})
    @ApiOperation(value = "Chỉnh sửa phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> updateReceiptImport(HttpServletRequest request,
                                                @ApiParam("Id đơn nhập hàng")@PathVariable long id,
                                                @RequestBody ReceiptUpdateRequest rq) {
        Response<Object> response = receiptService.updateReceiptImport(rq, id,this.getUserName());
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
        Response<String> response = receiptService.removeReceiptImport( id,type,this.getUserName());
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
        Response<List<PoConfirmDTO>> response = receiptService.getListPoConfirm();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PO_CONFIRM_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/adjustment"})
    @ApiOperation(value = "Lấy danh sách phiếu nhập điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment(HttpServletRequest request) {
        Response<List<StockAdjustmentDTO>> response = receiptService.getListStockAdjustment();
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_STOCK_ADJUSTMENT_SUCCESS);
        return response;
    }


    @GetMapping(value = { V1 + root + "/borrowing"})
    @ApiOperation(value = "Lấy danh sách phiếu vay mượn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockBorrowingDTO>> getListStockBorrowing(HttpServletRequest request) {
        Response<List<StockBorrowingDTO>> response = receiptService.getListStockBorrowing(this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_STOCK_BORROWING_SUCCESS);
        return response;
    }


    @GetMapping(value = { V1 + root + "/po-detail0/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm bán của phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoId(HttpServletRequest request,
                                                           @ApiParam("Id đơn mua hàng")@PathVariable Long id) {
        Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> response = receiptService.getPoDetailByPoId(id,this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PRODUCT_FOR_SALE_OF_PO_CONFIRM_SUCCESS);
        return response;
    }


    @GetMapping(value = { V1 + root + "/po-detail1/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm khuyến mãi của phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoIdAndPriceIsNull(
                                                HttpServletRequest request,
                                                @ApiParam("Id đơn mua hàng")@PathVariable Long id) {
        Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> response = receiptService.getPoDetailByPoIdAndPriceIsNull(id,this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_PRODUCT_PROMOTION_OF_PO_CONFIRM_SUCCESS);
        return response;
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
        Response<CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse>> response = receiptService.getStockAdjustmentDetail(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_ADJUSTMENT_DETAIL_SUCCESS);
        return response;
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
        Response<CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse>> response = receiptService.getStockBorrowingDetail(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_BORROWING_DETAIL_SUCCESS);
        return response;
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
        Response<Object> response = receiptService.getTransDetail(type,id,this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_RECEIPT_IMPORT_DETAIL_SUCCESS);
        return response;
    }

    @GetMapping(V1 + root +"/warehouse-type")
    @ApiOperation(value = "Lấy kho mặc định của cửa hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<WareHouseTypeDTO>  getWareHouseType(HttpServletRequest request) {
        Response<WareHouseTypeDTO> response = receiptService.getWareHouseTypeName(this.getShopId());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_DEFAULT_WARE_HOUSE_SUCCESS);
        return response;
    }

    @PutMapping(value = { V1 + root + "/not-import/{Id}"})
    @ApiOperation(value = "Xét phiếu mua hàng thành không nhập")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> setNotImport(HttpServletRequest request,
                                @ApiParam("Id phiếu mua hàng")@PathVariable long Id,
                                @RequestBody NotImportRequest rq) {
        Response<String> response = receiptService.setNotImport(Id,rq);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.SET_PO_CONFIRM_NOT_IMPORT_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/excel/{poId}"})
    @ApiOperation(value = "Xuất excel phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(
                    @ApiParam("Id phiếu mua hàng")@PathVariable Long poId) throws IOException {

        CoverResponse<List<PoDetailDTO>,TotalResponse> soConfirmList = receiptService.getPoDetailByPoId(poId,this.getShopId()).getData();
        List<PoDetailDTO> list1 = soConfirmList.getResponse();
        CoverResponse<List<PoDetailDTO>,TotalResponse> soConfirmList2 = receiptService.getPoDetailByPoIdAndPriceIsNull(poId,this.getShopId()).getData();
        List<PoDetailDTO> list2 = soConfirmList2.getResponse();
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        ShopDTO shops = shopClient.getByIdV1(shop.getParentShopId()).getData();
        ExportExcel exportExcel = new ExportExcel(list1,list2,shops);
        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=PoDetail.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
