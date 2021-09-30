package vn.viettel.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.report.messaging.ChangePriceFilter;
import vn.viettel.report.messaging.ChangePriceReportRequest;
import vn.viettel.report.service.ChangePriceReportService;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePricePrintDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;
import vn.viettel.report.service.excel.ChangePriceReportExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public Object index(HttpServletRequest request, @RequestParam(required = false) String licenseNumber, @RequestParam Date fromTransDate,
                                                                                    @RequestParam Date toTransDate, @RequestParam(required = false) Date fromOrderDate,  @RequestParam(required = false) Date toOrderDate,
                                                                                    @RequestParam(required = false) String productCodes, Pageable pageable, @RequestParam Boolean isPaging) throws ParseException {

        ChangePriceFilter filter = new ChangePriceFilter(licenseNumber, this.getShopId(request),
                DateUtils.convertFromDate(fromTransDate), DateUtils.convertFromDate(toTransDate), DateUtils.convertFromDate(fromOrderDate), DateUtils.convertFromDate(toOrderDate), productCodes);
        Object result = service.index(filter, pageable, isPaging);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.SEARCH_PRICE_CHANGED_SUCCESS);
        return result;
    }

    @ApiOperation(value = "Api dùng để lấy dữ liệu báo cáo cho xuất file pdf")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(V1 + root + "/pdf")
    public Response<ChangePricePrintDTO> getAll(HttpServletRequest request,
                                                @RequestParam(required = false) String licenseNumber,
                                                @RequestParam Date fromTransDate,
                                                @RequestParam Date toTransDate,
                                                @RequestParam(required = false) Date fromOrderDate,
                                                @RequestParam(required = false) Date toOrderDate,
                                                @RequestParam(required = false) String ids,
                                                Pageable pageable) throws ParseException {
        ChangePriceFilter filter = new ChangePriceFilter(licenseNumber, this.getShopId(request),
                DateUtils.convertFromDate(fromTransDate), DateUtils.convertFromDate(toTransDate), DateUtils.convertFromDate(fromOrderDate), DateUtils.convertFromDate(toOrderDate), ids);
        ChangePricePrintDTO result = service.getAll(filter, pageable);
        LogFile.logToFile(appName, getUsername(request), LogLevel.INFO, request, LogMessage.GET_LIST_PRICE_CHANGED_SUCCESS);
        return new Response<ChangePricePrintDTO>().withData(result);
    }

    @ApiOperation(value = "Api dùng để xuất excel cho báo cáo chênh lệch giá")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/excel"})
    public void exportToExcel(HttpServletRequest httpRequest, @RequestParam(required = false) String licenseNumber, @RequestParam Date fromTransDate, @RequestParam Date toTransDate,
                               @RequestParam(required = false) Date fromOrderDate, @RequestParam(required = false) Date toOrderDate, @RequestParam(required = false) String ids,
                                        HttpServletResponse response,Pageable pageable) throws IOException, ParseException {
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId(httpRequest)).getData();
        if(shop == null) throw new ValidateException(ResponseMessage.SHOP_NOT_FOUND);
        ChangePriceFilter filter = new ChangePriceFilter(licenseNumber, this.getShopId(httpRequest),
                DateUtils.convertFromDate(fromTransDate), DateUtils.convertFromDate(toTransDate), DateUtils.convertFromDate(fromOrderDate), DateUtils.convertFromDate(toOrderDate), ids);

        Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>> listData = (Response<CoverResponse<List<ChangePriceDTO>, ChangePriceTotalDTO>>) service.index(filter, pageable, false);
        ChangePriceReportRequest input = new ChangePriceReportRequest(listData.getData().getInfo(), listData.getData().getResponse());
        ChangePriceReportExcel exportExcel = new ChangePriceReportExcel(input, shop, shop.getParentShop(), DateUtils.convert2Local(fromTransDate), DateUtils.convert2Local(toTransDate));
        this.closeStreamExcel(response, exportExcel.export(), "BC_chenh_lech_gia_");
        response.getOutputStream().flush();
    }
}
