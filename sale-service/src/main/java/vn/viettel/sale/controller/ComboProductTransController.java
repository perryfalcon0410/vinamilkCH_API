package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.ComboProductTransService;
import vn.viettel.sale.service.dto.ComboProductTranDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

@RestController
@Api(tags = "API xuất nhập sản phẩm combo")
public class ComboProductTransController extends BaseController {

    @Autowired
    ComboProductTransService comboProductTransService;
    private final String root = "/sales/combo-product-trans";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Tìm kiếm xuất nhập sản phẩm combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> findComboProductTrans(HttpServletRequest request,
                                      @ApiParam("Tìm mã giao dịch")
                                      @RequestParam(value = "transCode", required = false, defaultValue = "") String transCode,
                                      @ApiParam("Loại giao dịch")
                                      @RequestParam(value = "transType", required = false) Integer transType,
                                      @RequestParam(value = "fromDate", required = false) Date fromDate,
                                      @RequestParam(value = "toDate", required = false) Date toDate,
                                      Pageable pageable) {

        ComboProductTranFilter filter = new ComboProductTranFilter(this.getShopId(), transCode, transType, fromDate, toDate);
        Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> response = comboProductTransService.findAll(filter, pageable);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_COMBO_PRODUCTS_TRANS_SUCCESS);
        return response;
    }

    @PostMapping(value = { V1 + root} )
    @ApiOperation(value = "Tạo mới xuất nhập sản phẩm combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<ComboProductTranDTO> create(HttpServletRequest request, @Valid @RequestBody ComboProductTranRequest comboRequest) {
        Response<ComboProductTranDTO> response = comboProductTransService.create(comboRequest, this.getShopId(), this.getUserName());
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.CREATE_COMBO_PRODUCT_TRANS_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn xuất nhập sản phẩm combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<ComboProductTranDTO> getComboProductTran(HttpServletRequest request, @PathVariable Long id) {
        Response<ComboProductTranDTO> response = comboProductTransService.getComboProductTrans(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_COMBO_PRODUCT_TRANS_SUCCESS);
        return response;
    }
}
