package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.excel.StockCountingAllExcel;
import vn.viettel.sale.excel.StockCountingFailExcel;
import vn.viettel.sale.excel.StockCountingFilledExcel;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@RestController
@Api(tags = "Api sử dụng cho quản lý kiểm kê")
public class InventoryController extends BaseController {

    @Autowired
    InventoryService inventoryService;
    @Autowired
    ShopClient shopClient;
    private final String root = "/sales";

    @ApiOperation(value = "Api dùng để lấy danh sách tồn kho theo điều kiện tìm kiếm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/inventory"})
    public Response<Page<StockCountingDTO>> index(@RequestParam(value = "stockCountingCode",required = false) String stockCountingCode,
                                                  @RequestParam(value ="warehouseTypeId",required = false) Long warehouseTypeId,
                                                  @RequestParam(value = "fromDate",required = false) Date fromDate,
                                                  @RequestParam(value = "toDate",required = false) Date toDate, Pageable pageable) {
        return inventoryService.index(stockCountingCode,warehouseTypeId, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate),pageable);
    }

    @ApiOperation(value = "Api dùng để lấy tất cả sản phẩm tồn kho")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/inventories"})
    public Object getAll(@PageableDefault(value = 2000)Pageable pageable,
                         @RequestParam Boolean isPaging,
                         @RequestParam(value = "searchKeywords",required = false) String searchKeywords) {
        Object response = inventoryService.getAll(pageable, isPaging, searchKeywords);
        return new Response<>().withData(response);
    }

    @ApiOperation(value = "Api dùng để lấy phiếu kiểm kê chi tiết")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 1005, message = "Không tìm thấy phiếu kiểm kê"),
            @ApiResponse(code = 9019, message = "Sản phẩm không tồn tại"),
            @ApiResponse(code = 1007, message = "Không tìm thấy thông tin sản phẩm")
    })
    @GetMapping(value = { V1 + root + "/inventory/{id}"})
    public Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>> getStockCountingDetails(@PathVariable Long id, Pageable pageable) {
        return inventoryService.getByStockCountingId(id, pageable);
    }

    @ApiOperation(value = "Api dùng để import excel phiếu kiểm kê")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/inventory/import-excel"})
    public Response<CoverResponse<StockCountingImportDTO, InventoryImportInfo>> importExcel(HttpServletRequest httpRequest,
                                                                                            @RequestParam(name = "file") MultipartFile file,
                                                                                            @RequestParam(value = "searchKeywords",required = false) String searchKeywords,
                                                                                            @PageableDefault(value = 2000)Pageable pageable) throws IOException {
        CoverResponse<StockCountingImportDTO, InventoryImportInfo> response = inventoryService.importExcel(file, pageable, searchKeywords);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, httpRequest, LogMessage.EXPORT_EXCEL_REPORT_EXPORT_GOODS_SUCCESS);
        return new Response<CoverResponse<StockCountingImportDTO, InventoryImportInfo>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để cập nhật phiếu kiểm kê")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 1005, message = "Không tìm thấy phiếu kiểm kê")
    })
    @PutMapping(value = { V1 + root + "/inventory/{id}"})
    public Response<List<StockCountingDetail>> updateStockCounting(@PathVariable Long id,
                                                                   @RequestBody List<StockCountingUpdateDTO> details) {
        List<StockCountingDetail> list = inventoryService.updateStockCounting(id, this.getUserName(), details);
        return new Response<List<StockCountingDetail>>().withData(list);
    }

    @GetMapping(value = { V1 + root + "/filled-stock/export"})
    @ApiOperation(value = "Xuất excel kiểm kê")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public ResponseEntity stockCountingExport(@RequestParam (value = "id") Long id) throws IOException {
        List<StockCountingExcel> export = inventoryService.getByStockCountingId(id, null).getData().getResponse().getContent();
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        StockCountingFilledExcel stockCountingFilledExcel =
                new StockCountingFilledExcel(export, shop, LocalDateTime.now());
        ByteArrayInputStream in = stockCountingFilledExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Stock_Counting_Filled.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @PostMapping(value = { V1 + root + "/filled-stock/export-fail"})
    @ApiOperation(value = "Xuất excel sản phẩm không import dc")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public ResponseEntity stockCountingExportFail(@RequestParam(name = "file") MultipartFile file,
                                                  @RequestParam(value = "searchKeywords",required = false) String searchKeywords,
                                                  @PageableDefault(value = 2000)Pageable pageable) throws IOException {
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        CoverResponse<StockCountingImportDTO, InventoryImportInfo> data = inventoryService.importExcel(file, pageable, searchKeywords);
        StockCountingFailExcel stockCountingFailExcel =
                new StockCountingFailExcel(data.getResponse().getImportFails(), shop, LocalDateTime.now());
        ByteArrayInputStream in = stockCountingFailExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Stock_Counting_Fail.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @GetMapping(value = { V1 + root + "/filled-stock/export-all"})
    @ApiOperation(value = "Xuất excel tất cả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public ResponseEntity stockCountingExportAll() throws IOException {
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> data = (CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>) inventoryService.getAll(null, false, null);
        List<StockCountingDetailDTO> listAll = data.getResponse();
        StockCountingAllExcel stockCountingAll =
                new StockCountingAllExcel(listAll, shop, LocalDateTime.now());
        ByteArrayInputStream in = stockCountingAll.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Stock_Counting_All.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @ApiOperation(value = "Api dùng để tạo mới phiếu kiểm kê")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 2004, message = "Danh sách rỗng"),
            @ApiResponse(code = 203, message = "Hủy thêm mới")
    })
    @PostMapping(value = { V1 + root + "/inventory"})
    public Object createStockCounting(@RequestBody List<StockCountingDetailDTO> stockCountingDetails,
                                             @RequestParam(required = false) Boolean override) {
        return inventoryService.createStockCounting(stockCountingDetails, this.getUserId(), this.getShopId(), override);
    }

    @GetMapping(value = {V1 + root + "/inventory/numInDay"})
    public Response<Boolean> getInventoryNumberInDay() {
        Response<Boolean> check = new Response<>();
        return check.withData(inventoryService.checkInventoryInDay(this.getShopId()));
    }
}
