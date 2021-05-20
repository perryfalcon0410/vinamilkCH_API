package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import vn.viettel.core.dto.sale.WareHouseTypeDTO;
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
        private final String root = "/sales/import";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Lấy danh sách phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ReceiptImportListDTO>, TotalResponse>> find(
                                @RequestParam(value ="redInvoiceNo", required = false ) String redInvoiceNo,
                                @RequestParam(value ="fromDate",required = false) Date fromDate,
                                @RequestParam(value ="toDate",required = false) Date toDate,
                                @RequestParam(value ="type", required = false ) Integer type, Pageable pageable) {
        return receiptService.find(redInvoiceNo,fromDate,toDate,type,this.getShopId(),pageable);
    }

    @PostMapping(value = { V1 + root })
    @ApiOperation(value = "Tạo phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> createReceipt(@Valid @RequestBody ReceiptCreateRequest request) {
        return receiptService.createReceipt(request,this.getUserId(),this.getShopId());
    }


    @GetMapping(value = { V1 + root + "/trans/{id}"})
    @ApiOperation(value = "Lấy thông tin phiếu nhập hàng dùng để chỉnh sửa hoặc xem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> getTrans(@PathVariable(name = "id") Long id,@RequestParam Integer type) {
        return receiptService.getForUpdate(type,id);
    }


    @PatchMapping(value = { V1 + root + "/update/{id}"})
    @ApiOperation(value = "Chỉnh sửa phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> updateReceiptImport(@RequestBody ReceiptUpdateRequest request, @PathVariable long id) {
        return receiptService.updateReceiptImport(request, id,this.getUserName());
    }


    @PatchMapping(value = { V1 + root + "/remove/{id}"})
    @ApiOperation(value = "Xóa phiếu nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> removeReceiptImport(@PathVariable long id,@RequestParam Integer type ) {
        return receiptService.removeReceiptImport( id,type,this.getUserName());
    }


    @GetMapping(value = { V1 + root + "/po-confirm"})
    @ApiOperation(value = "Lấy danh sách phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<PoConfirmDTO>> getListPoConfirm() {
        return receiptService.getListPoConfirm();
    }


    @GetMapping(value = { V1 + root + "/adjustment"})
    @ApiOperation(value = "Lấy danh sách phiếu nhập điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment() {
        return receiptService.getListStockAdjustment();
    }


    @GetMapping(value = { V1 + root + "/borrowing"})
    @ApiOperation(value = "Lấy danh sách phiếu vay mượn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockBorrowingDTO>> getListStockBorrowing() {
        return receiptService.getListStockBorrowing(this.getShopId());
    }


    @GetMapping(value = { V1 + root + "/po-detail0/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm bán của phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoId(@PathVariable Long id) {
        return receiptService.getPoDetailByPoId(id,this.getShopId());
    }


    @GetMapping(value = { V1 + root + "/po-detail1/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm khuyến mãi của phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<PoDetailDTO>,TotalResponse>> getPoDetailByPoIdAndPriceIsNull(@PathVariable Long id) {
        return receiptService.getPoDetailByPoIdAndPriceIsNull(id,this.getShopId());
    }


    @GetMapping(value = { V1 + root + "/adjustment-detail/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm của phiếu điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<StockAdjustmentDetailDTO>, TotalResponse>> getStockAdjustmentDetail(@PathVariable Long id) {
        return receiptService.getStockAdjustmentDetail(id);
    }


    @GetMapping(value = { V1 + root + "/borrowing-detail/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm của phiếu vay mượn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<List<StockBorrowingDetailDTO>, TotalResponse>> getStockBorrowingDetail(@PathVariable Long id) {
        return receiptService.getStockBorrowingDetail(id);
    }


    @GetMapping(value = { V1 + root + "/trans-detail/{id}"})
    @ApiOperation(value = "Lấy danh sách sản phẩm của phiếu giao dịch nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> getPoTransDetail(@PathVariable Long id, @RequestParam Integer type) {
        return receiptService.getTransDetail(type,id,this.getShopId());
    }

    @GetMapping(V1 + root +"/warehouse-type")
    @ApiOperation(value = "Lấy danh sách loại kho")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<WareHouseTypeDTO>  getWareHouseType() {
        return receiptService.getWareHouseTypeName(this.getShopId());
    }

    @PutMapping(value = { V1 + root + "/not-import/{Id}"})
    @ApiOperation(value = "Xét phiếu mua hàng thành không nhập")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> setNotImport(@PathVariable long Id,@RequestBody NotImportRequest request) {
        return receiptService.setNotImport(Id,request);
    }

    @GetMapping(value = { V1 + root + "/excel/{poId}"})
    @ApiOperation(value = "Xuất excel phiếu mua hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(@PathVariable Long poId) throws IOException {

        CoverResponse<List<PoDetailDTO>,TotalResponse> soConfirmList = receiptService.getPoDetailByPoId(poId,this.getShopId()).getData();
        List<PoDetailDTO> list1 = soConfirmList.getResponse();
        CoverResponse<List<PoDetailDTO>,TotalResponse> soConfirmList2 = receiptService.getPoDetailByPoIdAndPriceIsNull(poId,this.getShopId()).getData();
        List<PoDetailDTO> list2 = soConfirmList2.getResponse();
        ExportExcel exportExcel = new ExportExcel(list1,list2);
        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=PoDetail.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
