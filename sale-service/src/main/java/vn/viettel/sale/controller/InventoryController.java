package vn.viettel.sale.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.excel.SampleExcel;
import vn.viettel.sale.excel.StockCountingAllExcel;
import vn.viettel.sale.excel.StockCountingFailExcel;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.InventoryImportInfo;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.StockCountingDetailDTO;
import vn.viettel.sale.service.dto.StockCountingExcelDTO;
import vn.viettel.sale.service.dto.StockCountingImportDTO;
import vn.viettel.sale.service.dto.StockCountingUpdateDTO;
import vn.viettel.sale.service.dto.TotalStockCounting;
import vn.viettel.sale.service.feign.ShopClient;


@RestController
@Api(tags = "Api sử dụng cho quản lý kiểm kê")
public class InventoryController extends BaseController {

    @Autowired
    InventoryService inventoryService;
    @Autowired
    ShopClient shopClient;
    private final String root = "/sales";

    public void setService(InventoryService service){
        if(inventoryService == null) inventoryService = service;
    }

    @ApiOperation(value = "Api dùng để lấy danh sách tồn kho theo điều kiện tìm kiếm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/inventory"})
    public Response<Page<StockCountingDTO>> index(HttpServletRequest httpRequest, @RequestParam(value = "stockCountingCode",required = false) String stockCountingCode,
                                                  @RequestParam(value ="wareHouseTypeId",required = false) Long wareHouseTypeId,
                                                  @RequestParam(value = "fromDate") Date fromDate,
                                                  @RequestParam(value = "toDate") Date toDate,
                                                  @SortDefault.SortDefaults({
                                                      @SortDefault(sort = "countingDate", direction = Sort.Direction.DESC),
                                                  }) Pageable pageable) {
        Page<StockCountingDTO> response = inventoryService.index(stockCountingCode,wareHouseTypeId, DateUtils.convertFromDate(fromDate), DateUtils.convertToDate(toDate),this.getShopId(httpRequest),pageable);
        return new Response<Page<StockCountingDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để lấy tất cả sản phẩm tồn kho")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/inventories"})
    public Object getAll(HttpServletRequest httpRequest, @RequestParam(value = "searchKeywords",required = false) String searchKeywords,
                          @RequestParam(value = "wareHouseTypeId",required = false) Long wareHouseTypeId) {
        Object response = inventoryService.getAll(getShopId(httpRequest), searchKeywords,wareHouseTypeId);
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
    public Response<CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting>> getStockCountingDetails(HttpServletRequest httpRequest, @PathVariable Long id) {
        CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting> response = inventoryService.getByStockCountingId(id, this.getShopId(httpRequest));
        return new Response<CoverResponse<List<StockCountingExcelDTO>, TotalStockCounting>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để import excel phiếu kiểm kê")
    @ApiResponse(code = 200, message = "Success")
    @PostMapping(value = { V1 + root + "/inventory/import-excel"})
    public Response<CoverResponse<StockCountingImportDTO, InventoryImportInfo>> importExcel(HttpServletRequest httpRequest,
                                                                                            @RequestParam(name = "file") MultipartFile file,
                                                                                            @RequestParam(value = "searchKeywords",required = false) String searchKeywords,
                                                                                            @RequestParam(value = "wareHouseTypeId") Long wareHouseTypeId,
                                                                                            @PageableDefault(value = 2000)Pageable pageable) throws IOException {
        CoverResponse<StockCountingImportDTO, InventoryImportInfo> response = inventoryService.importExcel(getShopId(httpRequest), file, pageable, searchKeywords,wareHouseTypeId);
        LogFile.logToFile(appName, getUsername(httpRequest), LogLevel.INFO, httpRequest, LogMessage.EXPORT_EXCEL_REPORT_EXPORT_GOODS_SUCCESS);
        return new Response<CoverResponse<StockCountingImportDTO, InventoryImportInfo>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để cập nhật phiếu kiểm kê")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 1005, message = "Không tìm thấy phiếu kiểm kê")
    })
    @PutMapping(value = { V1 + root + "/inventory/{id}"})
    public Response<String> updateStockCounting(HttpServletRequest httpRequest,@PathVariable Long id,
                                                                   @RequestBody List<StockCountingUpdateDTO> details) {
        ResponseMessage message = inventoryService.updateStockCounting(id, this.getShopId(httpRequest), this.getUsername(httpRequest), details);
        Response response = new Response();
        if(message != null) {
            response.setStatusValue(message.statusCodeValue());
            response.setStatusCode(message.statusCode());
        }
        return response;
    }

    @GetMapping(value = { V1 + root + "/filled-stock/export"})
    @ApiOperation(value = "Xuất excel kiểm kê")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public void stockCountingExport(HttpServletRequest httpRequest, @RequestParam (value = "id") Long id, HttpServletResponse response) throws IOException {
        this.closeStreamExcel(response,inventoryService.exportExcel(id, this.getShopId(httpRequest)), "Kiem_ke_");
        response.getOutputStream().flush();
    }

    @PostMapping(value = { V1 + root + "/filled-stock/export-fail"})
    @ApiOperation(value = "Xuất excel sản phẩm không import dc")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public void stockCountingExportFail(HttpServletRequest httpRequest, @RequestParam(name = "file") MultipartFile file,
                                                  @RequestParam(value = "searchKeywords",required = false) String searchKeywords,
                                                  @RequestParam(value = "wareHouseTypeId") Long wareHouseTypeId,
                                                  @PageableDefault(value = 2000)Pageable pageable, HttpServletResponse response) throws IOException {
        CoverResponse<StockCountingImportDTO, InventoryImportInfo> data = inventoryService.importExcel(getShopId(httpRequest), file, pageable, searchKeywords,wareHouseTypeId);
        StockCountingFailExcel stockCountingFailExcel = new StockCountingFailExcel(data.getResponse().getImportFails(), LocalDateTime.now());
        this.closeStreamExcel(response, stockCountingFailExcel.export(), "stock_counting_fail");
        response.getOutputStream().flush();
    }

    @GetMapping(value = { V1 + root + "/filled-stock/export-all"})
    @ApiOperation(value = "Xuất excel tất cả")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public void stockCountingExportAll(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        ShopDTO shop = shopClient.getByIdV1(this.getShopId(httpRequest)).getData();
        CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting> data = (CoverResponse<List<StockCountingDetailDTO>, TotalStockCounting>) inventoryService.getAll(getShopId(httpRequest),null,null);
        List<StockCountingDetailDTO> listAll = data.getResponse();
        StockCountingAllExcel stockCountingAll = new StockCountingAllExcel(listAll, shop, shop.getParentShop(), LocalDateTime.now());
        this.closeStreamExcel(response, stockCountingAll.export(), "stock_counting_all");
        response.getOutputStream().flush();
    }
    @GetMapping(value = { V1 + root + "/inventory/sample-excel"})
    @ApiOperation(value = "Xuất excel mẫu")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public void ExportSampleExcel(HttpServletResponse response) throws IOException {
        SampleExcel sampleExcel = new SampleExcel(LocalDateTime.now());
        this.closeStreamExcel(response, sampleExcel.export(), "Nhap_kiem_ke_mau_");
        response.getOutputStream().flush();
    }

    @ApiOperation(value = "Api dùng để tạo mới phiếu kiểm kê")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 2004, message = "Danh sách rỗng"),
            @ApiResponse(code = 203, message = "Hủy thêm mới")
    })
    @PostMapping(value = { V1 + root + "/inventory"})
    public Response<String> createStockCounting(HttpServletRequest httpRequest, @RequestBody List<StockCountingDetailDTO> stockCountingDetails,
                                             @RequestParam(value = "wareHouseTypeId") Long wareHouseTypeId,
                                             @RequestParam(required = false) Boolean override) {
        Long id = inventoryService.createStockCounting(stockCountingDetails, this.getUserId(httpRequest), this.getShopId(httpRequest),wareHouseTypeId, override);
        Response response = new Response();
        response.setStatusValue(ResponseMessage.CREATED_SUCCESSFUL.statusCodeValue());
        response.setData(id);
        return response;
    }

    @GetMapping(value = {V1 + root + "/inventory/numInDay/{wareHouseTypeId}"})
    public Response<Boolean> getInventoryNumberInDay(HttpServletRequest httpRequest, @PathVariable(value = "wareHouseTypeId") Long wareHouseTypeId) {
        Response<Boolean> check = new Response<>();
        return check.withData(inventoryService.checkInventoryInDay(wareHouseTypeId, this.getShopId(httpRequest)));
    }
}
