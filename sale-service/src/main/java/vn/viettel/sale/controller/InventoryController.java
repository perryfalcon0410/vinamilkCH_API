package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
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
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.util.StringUtils;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.excel.SampleExcel;
import vn.viettel.sale.excel.StockCountingAllExcel;
import vn.viettel.sale.excel.StockCountingFailExcel;
import vn.viettel.sale.excel.StockCountingFilledExcel;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                                                  @RequestParam(value = "toDate",required = false) Date toDate,
                                                  @SortDefault.SortDefaults({
                                                      @SortDefault(sort = "countingDate", direction = Sort.Direction.ASC),
                                                  }) Pageable pageable) {
        Page<StockCountingDTO> response = inventoryService.index(stockCountingCode,warehouseTypeId, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate),this.getShopId(),pageable);
        return new Response<Page<StockCountingDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để lấy tất cả sản phẩm tồn kho")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/inventories"})
    public Object getAll(@RequestParam(value = "searchKeywords",required = false) String searchKeywords) {
        Object response = inventoryService.getAll(getShopId(), searchKeywords);
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
    public Response<CoverResponse<List<StockCountingExcel>, TotalStockCounting>> getStockCountingDetails(@PathVariable Long id) {
        CoverResponse<List<StockCountingExcel>, TotalStockCounting> response = inventoryService.getByStockCountingId(id);
        return new Response<CoverResponse<List<StockCountingExcel>, TotalStockCounting>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để import excel phiếu kiểm kê")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/inventory/import-excel"})
    public Response<CoverResponse<StockCountingImportDTO, InventoryImportInfo>> importExcel(HttpServletRequest httpRequest,
                                                                                            @RequestParam(name = "file") MultipartFile file,
                                                                                            @RequestParam(value = "searchKeywords",required = false) String searchKeywords,
                                                                                            @PageableDefault(value = 2000)Pageable pageable) throws IOException {
        CoverResponse<StockCountingImportDTO, InventoryImportInfo> response = inventoryService.importExcel(getShopId(), file, pageable, searchKeywords);
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
    public void stockCountingExport(@RequestParam (value = "id") Long id, HttpServletResponse response) throws IOException {
        List<StockCountingExcel> export = inventoryService.getByStockCountingId(id).getResponse();
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        StockCountingFilledExcel stockCountingFilledExcel =
                new StockCountingFilledExcel(export, shop, LocalDateTime.now());
        ByteArrayInputStream in = stockCountingFilledExcel.export();

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=Kiem_ke_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @PostMapping(value = { V1 + root + "/filled-stock/export-fail"})
    @ApiOperation(value = "Xuất excel sản phẩm không import dc")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public void stockCountingExportFail(@RequestParam(name = "file") MultipartFile file,
                                                  @RequestParam(value = "searchKeywords",required = false) String searchKeywords,
                                                  @PageableDefault(value = 2000)Pageable pageable, HttpServletResponse response) throws IOException {
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        CoverResponse<StockCountingImportDTO, InventoryImportInfo> data = inventoryService.importExcel(getShopId(), file, pageable, searchKeywords);
        StockCountingFailExcel stockCountingFailExcel =
                new StockCountingFailExcel(data.getResponse().getImportFails(), shop, LocalDateTime.now());
        ByteArrayInputStream in = stockCountingFailExcel.export();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=stock_counting_fail_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @GetMapping(value = { V1 + root + "/filled-stock/export-all"})
    @ApiOperation(value = "Xuất excel tất cả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public void stockCountingExportAll( HttpServletResponse response) throws IOException {
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> data = (CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>) inventoryService.getAll(getShopId(),null);
        List<StockCountingDetailDTO> listAll = data.getResponse();
        StockCountingAllExcel stockCountingAll =
                new StockCountingAllExcel(listAll, shop, LocalDateTime.now());
        ByteArrayInputStream in = stockCountingAll.export();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=stock_counting_all_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();
    }
    @GetMapping(value = { V1 + root + "/inventory/sample-excel"})
    @ApiOperation(value = "Xuất excel mẫu")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public void ExportSampleExcel(HttpServletResponse response) throws IOException {
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        ShopDTO shop_ = shopClient.getByIdV1(shop.getParentShopId()).getData();
        SampleExcel sampleExcel =
                new SampleExcel(shop,shop_, LocalDateTime.now());
        ByteArrayInputStream in = sampleExcel.export();

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=Nhap_kiem_ke_mau_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @ApiOperation(value = "Api dùng để tạo mới phiếu kiểm kê")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 2004, message = "Danh sách rỗng"),
            @ApiResponse(code = 203, message = "Hủy thêm mới")
    })
    @PostMapping(value = { V1 + root + "/inventory"})
    public Response<String> createStockCounting(@RequestBody List<StockCountingDetailDTO> stockCountingDetails,
                                             @RequestParam(required = false) Boolean override) {
        Long id = inventoryService.createStockCounting(stockCountingDetails, this.getUserId(), this.getShopId(), override);
        Response response = new Response();
        response.setStatusValue(ResponseMessage.CREATED_SUCCESSFUL.statusCodeValue());
        response.setData(id);
        return response;
    }

    @GetMapping(value = {V1 + root + "/inventory/numInDay"})
    public Response<Boolean> getInventoryNumberInDay() {
        Response<Boolean> check = new Response<>();
        return check.withData(inventoryService.checkInventoryInDay(this.getShopId()));
    }
}
