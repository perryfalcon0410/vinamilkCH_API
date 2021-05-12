package vn.viettel.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
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
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.excel.ChangePriceReportExcel;
import vn.viettel.report.service.feign.ShopClient;

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
    public Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>> index(@RequestParam(required = false) String code, @RequestParam(required = false) Date fromTransDate,
                                                                                    @RequestParam(required = false) Date toTransDate, @RequestParam(required = false) Date fromOrderDate,
                                                                                    @RequestParam(required = false) Date toOrderDate, @RequestParam(required = false) String ids, Pageable pageable) throws ParseException {
        return service.index(code, fromTransDate, toTransDate, fromOrderDate, toOrderDate, ids, pageable);
    }

    @ApiOperation(value = "Api dùng để lấy dữ liệu báo cáo cho xuất file pdf")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(V1 + root + "/pdf")
    public Response<List<CoverResponse<ChangePriceTotalDTO, List<ChangePriceDTO>>>>  getAll(@RequestParam(required = false) String code, @RequestParam(required = false) Date fromTransDate,
                                                                                           @RequestParam(required = false) Date toTransDate, @RequestParam(required = false) Date fromOrderDate,
                                                                                           @RequestParam(required = false) Date toOrderDate, @RequestParam(required = false) String ids, Pageable pageable) throws ParseException {
        return service.getAll(code, fromTransDate, toTransDate, fromOrderDate, toOrderDate, ids, pageable);
    }

    @ApiOperation(value = "Api dùng để xuất excel cho báo cáo chênh lệch giá")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/excel"})
    public ResponseEntity exportToExcel(@RequestParam(required = false) String code, @RequestParam(required = false) Date fromTransDate,
                                        @RequestParam(required = false) Date toTransDate, @RequestParam(required = false) Date fromOrderDate,
                                        @RequestParam(required = false) Date toOrderDate, @RequestParam(required = false) String ids, Pageable pageable) throws IOException, ParseException {
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO> listData = service.index(code, fromTransDate, toTransDate, fromOrderDate, toOrderDate, ids, pageable).getData();
        ChangePriceReportRequest input = new ChangePriceReportRequest(listData.getInfo(), listData.getResponse().getContent());

        ChangePriceReportExcel exportExcel = new ChangePriceReportExcel(input, shop, fromTransDate, toTransDate);

        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
