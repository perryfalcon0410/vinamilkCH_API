package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.StockCountingDetail;
import vn.viettel.sale.service.InventoryService;
import vn.viettel.sale.service.dto.*;
import vn.viettel.sale.service.feign.ShopClient;
import vn.viettel.sale.service.impl.StockCountingFilledExporterImpl;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        return inventoryService.index(stockCountingCode,warehouseTypeId,fromDate,toDate,pageable);
    }

    @ApiOperation(value = "Api dùng để lấy tất cả sản phẩm tồn kho")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/inventories"})
    public Response<CoverResponse<Page<StockCountingExcel>, TotalStockCounting>> getAll(Pageable pageable) {
        return inventoryService.getAll(pageable);
    }

    @ApiOperation(value = "Api dùng để lấy phiếu kiểm kê chi tiết")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 1005, message = "Không tìm thấy phiếu kiểm kê"),
            @ApiResponse(code = 9019, message = "Sản phẩm không tồn tại"),
            @ApiResponse(code = 1007, message = "Không tìm thấy thông tin sản phẩm")
    })
    @GetMapping(value = { V1 + root + "/inventory/{id}"})
    public Response<CoverResponse<Page<StockCountingDetailDTO>, TotalStockCounting>> getStockCountingDetails(@PathVariable Long id, Pageable pageable) {
        return inventoryService.getByStockCountingId(id, pageable);
    }

    @ApiOperation(value = "Api dùng để xuất excel phiếu kiểm kê")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/inventory/import-excel"})
    public Response<StockCountingImportDTO> importExcel(@RequestBody List<StockCountingDetailDTO> stockCountingDetails,
                                                        @RequestParam String path) throws FileNotFoundException {
        return inventoryService.importExcel(stockCountingDetails, path);
    }

    @ApiOperation(value = "Api dùng để cập nhật phiếu kiểm kê")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 1005, message = "Không tìm thấy phiếu kiểm kê")
    })
    @PutMapping(value = { V1 + root + "/inventory/{id}"})
    public Response<List<StockCountingDetail>> updateStockCounting(@PathVariable Long id,
                                                                   @RequestBody List<StockCountingDetailDTO> details) {
        return inventoryService.updateStockCounting(id, this.getUserId(), details);
    }

    @GetMapping(value = { V1 + root + "/filled-stock/export"})
    public ResponseEntity stockCountingReport(@RequestBody List<StockCountingExcel> listFail) throws IOException {
        List<StockCountingExcel> stockCountingExcels = listFail;
        ShopDTO shop = shopClient.getByIdV1(this.getShopId()).getData();
        StockCountingFilledExporterImpl stockCountingFilledExporterImpl =
                new StockCountingFilledExporterImpl(stockCountingExcels, shop);
        ByteArrayInputStream in = stockCountingFilledExporterImpl.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Stock_Counting_Filled.xlsx");

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
    public StockCounting createStockCounting(@RequestBody List<StockCountingDetailDTO> stockCountingDetails,
                                             @RequestParam Boolean override) {
        return inventoryService.createStockCounting(stockCountingDetails, this.getUserId(), this.getShopId(), override);
    }
}
