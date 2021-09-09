package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.excel.ExportExcel;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ReceiptImportService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                                @ApiParam("Mã phiếu nhập")@RequestParam(value = "transCode",required = false) String transCode,
                                @ApiParam("Số hóa đơn") @RequestParam(value ="redInvoiceNo", required = false ) String redInvoiceNo,
                                @ApiParam("Từ ngày nhập")@RequestParam(value ="fromDate") Date fromDate,
                                @ApiParam("Đến ngày nhập")@RequestParam(value ="toDate") Date toDate,
                                @ApiParam("Loại nhập")@RequestParam(value ="type", required = false ) Integer type,
                                @SortDefault.SortDefaults({
                                        @SortDefault(sort = "transDate", direction = Sort.Direction.DESC),
                                        @SortDefault(sort = "transCode", direction = Sort.Direction.DESC)
                                })
                                Pageable pageable) {
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
        ResponseMessage message = receiptService.createReceipt(rq,this.getUserId(),this.getShopId());
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
        ResponseMessage message = receiptService.updateReceiptImport(rq, id,this.getUserName(),this.getShopId());
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
        ResponseMessage message = receiptService.removeReceiptImport( id,type,this.getUserName(),this.getShopId());
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
        ExportExcel exportExcel = new ExportExcel(list1,list2, shop.getParentShop());
        this.closeStreamExcel(response, exportExcel.export(), "Phieu_mua_hang_" + StringUtils.createExcelFileName());
        response.getOutputStream().flush();
    }
}
