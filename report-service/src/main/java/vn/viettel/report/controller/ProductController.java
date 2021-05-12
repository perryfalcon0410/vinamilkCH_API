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
import vn.viettel.core.security.anotation.RoleAdmin;
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
                                        @RequestParam(value = "onlineNumber", required = false, defaultValue = "") String onlineNumber,
                                        @RequestParam(value = "fromDate", required = false) Date fromDate,
                                        @RequestParam(value = "toDate", required = false) Date toDate,
                                        @RequestParam(value = "productIds", required = false) String productIds) throws IOException {

        PromotionProductFilter filter = new PromotionProductFilter(this.getShopId(), onlineNumber, fromDate, toDate, productIds);
        ByteArrayInputStream in = promotionProductService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=promotion.xlsx");
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @GetMapping(V1 + root + "/promotions")
    @ApiOperation(value = "Danh sách dữ liệu báo cáo hàng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<PromotionProductDTO>, PromotionProductTotalDTO>> getReportPromotionProducts(
                                        HttpServletRequest request,
                                        @RequestParam(value = "onlineNumber", required = false, defaultValue = "") String onlineNumber,
                                        @RequestParam(value = "fromDate", required = false) Date fromDate,
                                        @RequestParam(value = "toDate", required = false) Date toDate,
                                        @RequestParam(value = "productCodes", required = false) String productCodes, Pageable pageable) {
        PromotionProductFilter filter = new PromotionProductFilter(this.getShopId(), onlineNumber, fromDate, toDate, productCodes);
        Response<CoverResponse<Page<PromotionProductDTO>, PromotionProductTotalDTO>> response = promotionProductService.getReportPromotionProducts(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return response;
    }


    @GetMapping(V1 + root + "/promotions/print")
    @ApiOperation(value = "Danh sách dữ liệu in báo cáo hàng khuyến mãi")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<PromotionProductReportDTO> getDataPrint(HttpServletRequest request,
                                        @RequestParam(value = "onlineNumber", required = false, defaultValue = "") String onlineNumber,
                                        @RequestParam(value = "fromDate", required = false) Date fromDate,
                                        @RequestParam(value = "toDate", required = false) Date toDate,
                                        @RequestParam(value = "productIds", required = false) String productIds, Pageable pageable) {
        PromotionProductFilter filter = new PromotionProductFilter(this.getShopId(), onlineNumber, fromDate, toDate, productIds);
        Response<PromotionProductReportDTO> response = promotionProductService.getDataPrint(filter);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.LOGIN_SUCCESS);
        return response;
    }

}
