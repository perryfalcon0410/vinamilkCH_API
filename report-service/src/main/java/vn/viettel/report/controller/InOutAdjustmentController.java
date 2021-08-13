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
import vn.viettel.core.util.StringUtils;
import vn.viettel.report.messaging.InOutAdjustmentFilter;
import vn.viettel.report.service.InOutAdjustmentService;
import vn.viettel.report.service.dto.InOutAdjusmentDTO;
import vn.viettel.report.service.dto.InOutAdjustmentTotalDTO;
import vn.viettel.report.service.excel.InOutAdjustmentExcel;
import vn.viettel.report.service.feign.ShopClient;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class InOutAdjustmentController extends BaseController {
    @Autowired
    ShopClient shopClient;
    @Autowired
    InOutAdjustmentService inOutAdjustmentService;
    private final String root = "/reports/in-out-adjustment";
    @GetMapping(V1 + root )
    @ApiOperation(value = "Danh sách nhập xuất điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<InOutAdjusmentDTO>, InOutAdjustmentTotalDTO>> find (@RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam(value = "productCodes",required = false) String productCodes, Pageable pageable) {
        InOutAdjustmentFilter filter = new InOutAdjustmentFilter(fromDate, toDate, productCodes,this.getShopId());
        CoverResponse<Page<InOutAdjusmentDTO>, InOutAdjustmentTotalDTO> dtos = inOutAdjustmentService.find(filter,pageable);
        return new Response<CoverResponse<Page<InOutAdjusmentDTO>, InOutAdjustmentTotalDTO>>().withData(dtos);
    }
    @GetMapping(value = { V1 + root+ "/excel"})
    @ApiOperation(value = "Xuất excel báo cáo nhập xuất điều chỉnh")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public void exportToExcel(@RequestParam Date fromDate, @RequestParam Date toDate, @RequestParam(value = "productCodes",required = false) String productCodes,Pageable pageable, HttpServletResponse response) throws IOException {
        ShopDTO shop = shopClient.getShopByIdV1(this.getShopId()).getData();
        InOutAdjustmentFilter filter = new InOutAdjustmentFilter(fromDate, toDate, productCodes,this.getShopId());
        List<InOutAdjusmentDTO> data = inOutAdjustmentService.dataExcel(filter);
        InOutAdjustmentExcel inOutAdjustmentExcel = new InOutAdjustmentExcel(data,shop,filter);
        ByteArrayInputStream in = inOutAdjustmentExcel.export();

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=BC_nhap_xuat_dieu_chinh_" + StringUtils.createExcelFileName());
        FileCopyUtils.copy(in, response.getOutputStream());
        in.close();
        response.getOutputStream().flush();
    }
}
