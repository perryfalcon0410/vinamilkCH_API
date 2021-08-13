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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.service.StockTotalReportService;
import vn.viettel.report.service.dto.StockTotalExcelRequest;
import vn.viettel.report.service.dto.StockTotalInfoDTO;
import vn.viettel.report.service.dto.StockTotalReportDTO;
import vn.viettel.report.service.dto.StockTotalReportPrintDTO;
import vn.viettel.report.service.excel.StockTotalReportExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@RestController
@Api(tags = "Api dùng cho báo cáo tồn kho")
public class StockTotalReportController extends BaseController {
    private final String root = "/reports/stock-total";
    @Autowired
    StockTotalReportService stockTotalReportService;
    @Autowired
    ShopClient shopClient;

    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO>> getStockTotalReport(@RequestParam Date stockDate,
                                                                                                     @RequestParam Long warehouseTypeId,
                                                                                                     @RequestParam(required = false) String productCodes,
                                                                                                     Pageable pageable) {
        CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO> response = stockTotalReportService.getStockTotalReport(stockDate, productCodes, this.getShopId(), warehouseTypeId, pageable);
        return new Response<CoverResponse<Page<StockTotalReportDTO>, StockTotalInfoDTO>>().withData(response);
    }

    @ApiOperation(value = "Api dùng để xuất excel cho báo cáo tồn kho")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = {V1 + root + "/excel"})
    public void exportToExcel(@RequestParam Date stockDate, @RequestParam(required = false) String productCodes,  @RequestParam Long warehouseTypeId, HttpServletResponse response) throws IOException {
        ByteArrayInputStream in = stockTotalReportService.exportExcel(stockDate, productCodes, this.getShopId(), warehouseTypeId);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=BC-ton_kho_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        in.close();
        response.getOutputStream().flush();
    }

    @ApiOperation(value = "In danh sách báo cáo tồn kho")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/print")
    public Response<StockTotalReportPrintDTO> print(@RequestParam Date stockDate,
                                                    @RequestParam Long warehouseTypeId,
                                                    @RequestParam(required = false) String productCodes) {
        StockTotalReportPrintDTO response = stockTotalReportService.print(stockDate, productCodes, this.getShopId(), warehouseTypeId);
        return new Response<StockTotalReportPrintDTO>().withData(response);
    }
}
