package vn.viettel.report.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.DateUtils;
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.SaleDeliveryTypeFilter;
import vn.viettel.report.service.SaleDeliveryTypeService;
import vn.viettel.report.service.dto.SaleByDeliveryTypeDTO;
import vn.viettel.report.service.dto.SaleDeliTypeTotalDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
public class SaleDeliveryTypeController extends BaseController {
    private final String root = "/reports/delivery-type";
    @Autowired
    SaleDeliveryTypeService saleDeliveryTypeService;

    @ApiOperation(value = "Xuất excel báo cáo doanh số theo loại giao hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root + "/excel")
    public void exportToExcel(
            HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
            @RequestParam(value = "apValue", required = false) String apValue,
            @RequestParam(value = "customerKW", required = false, defaultValue = "") String customerKW,
            @RequestParam(value = "phoneText", required = false, defaultValue = "") String phoneText,
            @RequestParam(value = "fromTotal", required = false) Float fromTotal,
            @RequestParam(value = "toTotal", required = false) Float toTotal
            , HttpServletResponse response
    ) throws Exception {
        SaleDeliveryTypeFilter filter =
                new SaleDeliveryTypeFilter(DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), this.getShopId(), orderNumber, apValue, customerKW, phoneText, fromTotal, toTotal);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.EXPORT_DATA_EXCEL_REPORT_SALE_DELIVERY_TYPE_SUCCESS);
        ByteArrayInputStream in = saleDeliveryTypeService.exportExcel(filter);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=BC_doanh_so_theo_loai_giao_hang_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        in.close();
        response.getOutputStream().flush();
    }

    @GetMapping(V1 + root + "/type")
    public Response<List<ApParamDTO>> deliveryType(){
        return new Response<List<ApParamDTO>>().withData(saleDeliveryTypeService.deliveryType());
    }

    @ApiOperation(value = "Danh sách báo cáo doanh số theo loại giao hàng")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(V1 + root)
    public Response<CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO>> listSaleDeliType(
            HttpServletRequest request,
            @RequestParam(value = "fromDate", required = false) Date fromDate,
            @RequestParam(value = "toDate", required = false) Date toDate,
            @RequestParam(value = "orderNumber", required = false, defaultValue = "") String orderNumber,
            @RequestParam(value = "apValue", required = false) String apValue,
            @RequestParam(value = "customerKW", required = false, defaultValue = "") String customerKW,
            @RequestParam(value = "phoneText", required = false, defaultValue = "") String phoneText,
            @RequestParam(value = "fromTotal", required = false) Float fromTotal,
            @RequestParam(value = "toTotal", required = false) Float toTotal, Pageable pageable){
        SaleDeliveryTypeFilter filter = new SaleDeliveryTypeFilter(DateUtils.convertFromDate(fromDate), DateUtils.convertFromDate(toDate), this.getShopId(), orderNumber, apValue, customerKW, phoneText, fromTotal, toTotal);
        CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO> response = saleDeliveryTypeService.getSaleDeliType(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.SEARCH_DATA_EXCEL_REPORT_SALE_DELIVERY_TYPE_SUCCESS);
        return new Response<CoverResponse<Page<SaleByDeliveryTypeDTO>, SaleDeliTypeTotalDTO>>().withData(response);
    }
}
