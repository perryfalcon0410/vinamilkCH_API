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
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.*;
import vn.viettel.sale.service.ReceiptExportService;
import vn.viettel.sale.service.dto.*;
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
             @RequestParam(value = "redInvoiceNo",required = false) String redInvoiceNo,
             @RequestParam(value = "fromDate") Date fromDate,
             @RequestParam(value = "toDate") Date toDate,
             @RequestParam(value = "type",required = false) Integer type,
             Pageable pageable) {
        return receiptExportService.find(redInvoiceNo,fromDate,toDate,type,this.getShopId(),pageable);
    }

    @PostMapping(value = { V1 + root })
    @ApiOperation(value = "Tạo phiếu xuất hàng phiếu xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> createReceipt( @RequestBody ReceiptExportCreateRequest request) {
        return receiptExportService.createReceipt(request, this.getUserId(),this.getShopId());
    }

    @PatchMapping(value = { V1 + root + "/update/{Id}"})
    @ApiOperation(value = "Chỉnh sửa phiếu xuất hàng phiếu xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Object> updateReceiptExport(@RequestBody ReceiptExportUpdateRequest request, @PathVariable long Id) {
        return receiptExportService.updateReceiptExport(request, Id);
    }


    @PutMapping(value = { V1 + root + "/remove/{Id}"})
    @ApiOperation(value = "Xóa phiếu xuất hàng phiếu xuất hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<String> removeReceiptExport(@RequestParam Integer type,@PathVariable long Id) {
        return receiptExportService.removeReceiptExport(type,Id);
    }


    @GetMapping(value = { V1 + root + "/po-trans"})
    @ApiOperation(value = "Danh sách phiếu nhập hàng dùng để xuất trả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<Page<PoTransDTO>> getListPoTrans(@RequestParam(value = "transCode",required = false) String transCode,
                                                     @RequestParam(value = "redInvoiceNo",required = false) String redInvoiceNo,
                                                     @RequestParam(value = "internalNumber",required = false) String internalNumber,
                                                     @RequestParam(value = "poNo",required = false) String poNo,
                                                     @RequestParam(value = "fromDate",required = false ) Date fromDate,
                                                     @RequestParam(value = "toDate",required = false) Date toDate, Pageable pageable) {
        return receiptExportService.getListPoTrans(transCode,redInvoiceNo,internalNumber,poNo,fromDate,toDate,pageable);
    }

    @GetMapping(value = { V1 + root + "/adjustment"})
    @ApiOperation(value = "Danh sách phiếu xuất điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockAdjustmentDTO>> getListStockAdjustment() {
        return receiptExportService.getListStockAdjustment();
    }

    @GetMapping(value = { V1 + root + "/borrowing"})
    @ApiOperation(value = "Danh sách phiếu xuất vay mượn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<StockBorrowingDTO>> getListStockBorrowing() {
        return receiptExportService.getListStockBorrowing(this.getShopId());
    }
}
