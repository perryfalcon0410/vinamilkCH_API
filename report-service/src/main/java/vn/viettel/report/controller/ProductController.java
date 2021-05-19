package vn.viettel.report.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import vn.viettel.report.messaging.PromotionProductFilter;
import vn.viettel.report.service.PromotionProductService;
import vn.viettel.report.service.dto.PromotionProductDTO;
import vn.viettel.report.service.dto.PromotionProductReportDTO;
import vn.viettel.report.service.dto.PromotionProductTotalDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@RestController
@Api(tags = "API báo cáo hàng khuyến mãi")
public class ProductController extends BaseController {
    private final String root = "/reports/products";

    @Autowired
    PromotionProductService promotionProductService;

    @GetMapping(V1 + root + "/promotions/excel")
    @ApiOperation(value = "Xuất excel báo cáo hàng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity exportToExcel(HttpServletRequest request,
                                        @ApiParam("Tìm theo số hóa đơn")
                                        @RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
                                        @RequestParam(value = "fromDate") Date fromDate,
                                        @RequestParam(value = "toDate") Date toDate,
                                        @ApiParam("Tìm theo danh sách mã sản phẩm")
                                        @RequestParam(value = "productCodes", required = false) String productCodes) throws IOException {

        PromotionProductFilter filter = new PromotionProductFilter(this.getShopId(), orderNumber, fromDate, toDate, productCodes);
        ByteArrayInputStream in = promotionProductService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=promotion.xlsx");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_REPORT_PROMOTION_PRODUCTS_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @GetMapping(V1 + root + "/promotions")
    @ApiOperation(value = "Danh sách dữ liệu báo cáo hàng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<PromotionProductDTO>, PromotionProductTotalDTO>> findReportPromotionProducts(
                                        HttpServletRequest request,
                                        @ApiParam("Tìm theo số hóa đơn")
                                        @RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
                                        @RequestParam(value = "fromDate") Date fromDate,
                                        @RequestParam(value = "toDate") Date toDate,
                                        @ApiParam("Tìm theo danh sách mã sản phẩm")
                                        @RequestParam(value = "productCodes", required = false) String productCodes, Pageable pageable) {
        PromotionProductFilter filter = new PromotionProductFilter(this.getShopId(), orderNumber, fromDate, toDate, productCodes);
        Response<CoverResponse<Page<PromotionProductDTO>, PromotionProductTotalDTO>> response = promotionProductService.getReportPromotionProducts(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_REPORT_PROMOTION_PRODUCTS_SUCCESS);
        return response;
    }

    @GetMapping(V1 + root + "/promotions/print")
    @ApiOperation(value = "Danh sách dữ liệu in báo cáo hàng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<PromotionProductReportDTO> getDataPrint(HttpServletRequest request,
                                        @ApiParam("Tìm theo số hóa đơn")
                                        @RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
                                        @RequestParam(value = "fromDate") Date fromDate,
                                        @RequestParam(value = "toDate") Date toDate,
                                        @ApiParam("Tìm theo danh sách mã sản phẩm")
                                        @RequestParam(value = "productCodes", required = false) String productCodes, Pageable pageable) {
        PromotionProductFilter filter = new PromotionProductFilter(this.getShopId(), orderNumber, fromDate, toDate, productCodes);
        Response<PromotionProductReportDTO> response = promotionProductService.getDataPrint(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.RETURN_DATA_PRINT_REPORT_PROMOTION_PRODUCTS_SUCCESS);
        return response;
    }

}
