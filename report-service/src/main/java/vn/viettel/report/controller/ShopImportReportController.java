package vn.viettel.report.controller;
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
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.service.excel.ShopImportExcel;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class ShopImportReportController extends BaseController {
    private final String root = "/reports/shop-import";
    @Autowired
    ShopImportReportService shopImportReportService;
    @Autowired
    ShopClient shopClient;


    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO>> index(@RequestParam(value = "fromDate",required = false) String fromDate, @RequestParam(value = "toDate",required = false) String toDate, @RequestParam(value = "productCodes",required = false) String productCodes,
                                                                                                @RequestParam(value = "importType",required = false) String importType, @RequestParam(value = "internalNumber",required = false)String internalNumber,
                                                                                                @RequestParam(value = "fromOrderDate",required = false) String fromOrderDate, @RequestParam(value = "toOrderDate",required = false) String toOrderDate, Pageable pageable) {
        ShopImportFilter shopImportFilter = new ShopImportFilter(fromDate, toDate, productCodes, importType,internalNumber,fromOrderDate,toOrderDate);
        CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO> response = shopImportReportService.find(shopImportFilter,pageable);
        return new Response<CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO>>().withData(response);
    }
    @GetMapping(value = { V1 + root+ "/excel"})
    @ApiOperation(value = "Xuất excel báo cáo nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public void exportToExcel(@RequestParam(value = "fromDate",required = false) String fromDate, @RequestParam(value = "toDate",required = false) String toDate, @RequestParam(value = "productCodes",required = false) String productCodes,
                                        @RequestParam(value = "importType",required = false) String importType, @RequestParam(value = "internalNumber",required = false)String internalNumber,
                                        @RequestParam(value = "fromOrderDate",required = false) String fromOrderDate, @RequestParam(value = "toOrderDate",required = false) String toOrderDate
                                        ,HttpServletResponse response) throws IOException {
        ShopImportFilter shopImportFilter = new ShopImportFilter(fromDate, toDate, productCodes, importType,internalNumber,fromOrderDate,toOrderDate);
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        ShopDTO shop_ = shopClient.getShopByIdV1(shop.getParentShopId()).getData();
        CoverResponse<List<ShopImportDTO>, ShopImportTotalDTO> data = shopImportReportService.dataExcel(shopImportFilter).getData();
        ShopImportExcel shopImportReport = new ShopImportExcel(data,shop,shop_,shopImportFilter);
        ByteArrayInputStream in = shopImportReport.export();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=report_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        response.getOutputStream().flush();
    }
}
