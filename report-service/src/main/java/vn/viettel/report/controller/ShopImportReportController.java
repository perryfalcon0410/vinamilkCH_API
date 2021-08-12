package vn.viettel.report.controller;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.service.dto.PrintShopImportDTO;
import vn.viettel.report.service.excel.ShopImportExcel;
import vn.viettel.report.messaging.ShopImportFilter;
import vn.viettel.report.service.ShopImportReportService;
import vn.viettel.report.service.dto.ShopImportDTO;
import vn.viettel.report.service.dto.ShopImportTotalDTO;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
public class ShopImportReportController extends BaseController {
    private final String root = "/reports/shop-import";
    @Autowired
    ShopImportReportService shopImportReportService;
    @Autowired
    ShopClient shopClient;


    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO>> index(@RequestParam(value = "fromDate",required = false) Date fromDate, @RequestParam(value = "toDate",required = false) Date toDate, @RequestParam(value = "productCodes",required = false) String productCodes,
                                                                                  @RequestParam(value = "importType",required = false) String importType, @RequestParam(value = "internalNumber",required = false)String internalNumber,
                                                                                  @RequestParam(value = "fromOrderDate",required = false) Date fromOrderDate, @RequestParam(value = "toOrderDate",required = false) Date toOrderDate, Pageable pageable) {
        ShopImportFilter shopImportFilter = new ShopImportFilter(DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), productCodes, importType,internalNumber,DateUtils.convertFromDate(fromOrderDate),DateUtils.convertFromDate(toOrderDate),this.getShopId());
        CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO> response = shopImportReportService.find(shopImportFilter,pageable);
        return new Response<CoverResponse<Page<ShopImportDTO>, ShopImportTotalDTO>>().withData(response);
    }
    @GetMapping(value = { V1 + root+ "/excel"})
    @ApiOperation(value = "Xuất excel báo cáo nhập hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public void exportToExcel(@RequestParam(value = "fromDate",required = false) Date fromDate, @RequestParam(value = "toDate",required = false) Date toDate, @RequestParam(value = "productCodes",required = false) String productCodes,
                                        @RequestParam(value = "importType",required = false) String importType, @RequestParam(value = "internalNumber",required = false)String internalNumber,
                                        @RequestParam(value = "fromOrderDate",required = false) Date fromOrderDate, @RequestParam(value = "toOrderDate",required = false) Date toOrderDate
                                        ,HttpServletResponse response) throws IOException, ParseException {
        ShopImportFilter shopImportFilter = new ShopImportFilter( DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), productCodes, importType,internalNumber,DateUtils.convertFromDate(fromOrderDate),DateUtils.convertFromDate(toOrderDate),this.getShopId());
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

    @GetMapping(V1 + root + "/print")
    public Response<PrintShopImportDTO> print(@RequestParam(value = "fromDate",required = false) Date fromDate,
                                              @RequestParam(value = "toDate",required = false) Date toDate,
                                              @RequestParam(value = "productCodes",required = false, defaultValue = "") String productCodes,
                                              @RequestParam(value = "importType",required = false) String importType,
                                              @RequestParam(value = "internalNumber",required = false, defaultValue = "")String internalNumber,
                                              @RequestParam(value = "fromOrderDate",required = false) Date fromOrderDate,
                                              @RequestParam(value = "toOrderDate",required = false) Date toOrderDate) {
        ShopImportFilter shopImportFilter = new ShopImportFilter(DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), productCodes, importType,internalNumber,DateUtils.convertFromDate(fromOrderDate),DateUtils.convertFromDate(toOrderDate),this.getShopId());
        PrintShopImportDTO response = shopImportReportService.print(shopImportFilter, this.getShopId());
        return new Response<PrintShopImportDTO>().withData(response);
    }
}
