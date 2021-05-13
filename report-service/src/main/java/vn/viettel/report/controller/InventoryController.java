package vn.viettel.report.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.InventoryImportExportFilter;
import vn.viettel.report.service.InventoryService;
import vn.viettel.report.service.dto.ImportExportInventoryDTO;
import vn.viettel.report.service.dto.ImportExportInventoryTotalDTO;
import vn.viettel.report.service.dto.PrintInventoryDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@RestController
@Api(tags = "API báo cáo xuất nhập tồn kho")
public class InventoryController extends BaseController {
    private final String root = "/reports/inventories";

    @Autowired
    InventoryService inventoryService;


    @GetMapping(V1 + root + "/import-export/excel")
    @ApiOperation(value = "Xuất excel báo cáo xuất nhập tồn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(HttpServletRequest request) throws IOException {
        ByteArrayInputStream in = inventoryService.exportImportExcel(this.getShopId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=inventory.xlsx");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_INVENTORY_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @GetMapping(V1 + root + "/import-export")
    @ApiOperation(value = "Danh sách dữ liệu báo cáo xuất nhập tồn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>> getReportInventoryImportExport(
                                                HttpServletRequest request,
                                                @RequestParam(value = "fromDate") Date fromDate,
                                                @RequestParam(value = "toDate") Date toDate,
                                                @RequestParam(value = "productCodes", required = false) String productCodes, Pageable pageable) {
        InventoryImportExportFilter filter = new InventoryImportExportFilter(this.getShopId(), fromDate, toDate, productCodes);
        Response<CoverResponse<Page<ImportExportInventoryDTO>, ImportExportInventoryTotalDTO>> response
                = inventoryService.getReportInventoryImportExport(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_INVENTORY_SUCCESS);
        return response;
    }

    @GetMapping(V1 + root + "/import-export/print")
    @ApiOperation(value = "Danh sách dữ liệu in báo cáo xuất nhập tồn")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<PrintInventoryDTO> getDataPrint(HttpServletRequest request,
                                                    @RequestParam(value = "fromDate") Date fromDate,
                                                    @RequestParam(value = "toDate") Date toDate,
                                                    @RequestParam(value = "productCodes", required = false) String productCodes) {
        InventoryImportExportFilter filter = new InventoryImportExportFilter(this.getShopId(), fromDate, toDate, productCodes);
        Response<PrintInventoryDTO> response = inventoryService.getDataPrint(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.RETURN_DATA_PRINT_REPORT_INVENTORY_SUCCESS);
        return response;
    }

}
