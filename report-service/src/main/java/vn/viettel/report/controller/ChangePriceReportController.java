package vn.viettel.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePricePrintDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.excel.ChangePriceReportExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "Api sử dụng để lấy dữ liệu báo cáo chênh lệch giá")
public class ChangePriceReportController extends BaseController {
    private final String root = "/reports/changePrices";

    @Autowired
    ChangePriceReportService service;
    @Autowired
    ShopClient shopClient;

    @ApiOperation(value = "Api dùng để lấy dữ liệu báo cáo chêch lệch giá theo điều kiện tìm kiếm")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(V1 + root)
    public Object index(HttpServletRequest request, @RequestParam(required = false) String code, @RequestParam Date fromTransDate,
                                                                                    @RequestParam Date toTransDate, @RequestParam Date fromOrderDate,  @RequestParam Date toOrderDate,
                                                                                    @RequestParam(required = false) String ids, Pageable pageable, @RequestParam Boolean isPaging) throws ParseException {
        Object result =
                service.index(code, DateUtils.convert2Local(fromTransDate), DateUtils.convert2Local(toTransDate),
                        DateUtils.convert2Local(fromOrderDate), DateUtils.convert2Local(toOrderDate), ids, pageable, isPaging);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.SEARCH_PRICE_CHANGED_SUCCESS);
        return result;
    }

    @ApiOperation(value = "Api dùng để lấy dữ liệu báo cáo cho xuất file pdf")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(V1 + root + "/pdf")
    public Response<ChangePricePrintDTO> getAll(HttpServletRequest request, @RequestParam(required = false) String code, @RequestParam Date fromTransDate,
                                                                                           @RequestParam Date toTransDate, @RequestParam Date fromOrderDate,
                                                                                           @RequestParam Date toOrderDate, @RequestParam(required = false) String ids,
                                                                                           Pageable pageable) throws ParseException {
        ChangePricePrintDTO result =
                service.getAll(code, this.getShopId(), DateUtils.convert2Local(fromTransDate),
                        DateUtils.convert2Local(toTransDate), DateUtils.convert2Local(fromOrderDate), DateUtils.convert2Local(toOrderDate), ids, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_LIST_PRICE_CHANGED_SUCCESS);
        return new Response<ChangePricePrintDTO>().withData(result);
    }

    @ApiOperation(value = "Api dùng để xuất excel cho báo cáo chênh lệch giá")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/excel"})
    public ResponseEntity exportToExcel(HttpServletRequest request, @RequestParam(required = false) String code, @RequestParam Date fromTransDate,
                                        @RequestParam Date toTransDate, @RequestParam Date fromOrderDate,
                                        @RequestParam Date toOrderDate, @RequestParam(required = false) String ids, Pageable pageable) throws IOException, ParseException {
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>> listData = (Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>>) service.index(
                code, DateUtils.convert2Local(fromTransDate), DateUtils.convert2Local(toTransDate), DateUtils.convert2Local(fromOrderDate), DateUtils.convert2Local(toOrderDate), ids, pageable, false);
        ChangePriceReportRequest input = new ChangePriceReportRequest(listData.getData().getInfo(), listData.getData().getResponse());

        ChangePriceReportExcel exportExcel = new ChangePriceReportExcel(input, shop, DateUtils.convert2Local(fromTransDate), DateUtils.convert2Local(toTransDate));

        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_PRICE_CHANGED_SUCCESS);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
