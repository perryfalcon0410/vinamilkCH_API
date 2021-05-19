package vn.viettel.report.controller;

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
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.CustomerNotTradeService;
import vn.viettel.report.service.dto.CustomerReportDTO;
import vn.viettel.report.service.excel.CustomerNotTradeExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class CustomerNotTradeReportController extends BaseController {
    @Autowired
    CustomerNotTradeService service;
    @Autowired
    ShopClient shopClient;

    private final String root = "/reports/customers";

    @GetMapping(V1 + root + "/not-trade")
    public Object getCustomerNotTrade(@RequestParam Date fromDate, @RequestParam Date toDate,
                                      @RequestParam Boolean isPaging, Pageable pageable) {
        return service.index(fromDate, toDate, isPaging, pageable);
    }

    @GetMapping(value = { V1 + root + "/not-trade/excel"})
    public ResponseEntity exportToExcel(HttpServletRequest request, @RequestParam(required = false) Date fromDate,
                                        @RequestParam(required = false) Date toDate, Pageable pageable) throws IOException{
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        Response<List<CustomerReportDTO>> listData = (Response<List<CustomerReportDTO>>) service.index(fromDate, toDate, false, pageable);

        CustomerNotTradeExcel exportExcel = new CustomerNotTradeExcel(listData.getData(), shop, fromDate, toDate);

        ByteArrayInputStream in = exportExcel.export();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_EXCEL_CUSTOMER_NOT_TRADE_SUCCESS);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
