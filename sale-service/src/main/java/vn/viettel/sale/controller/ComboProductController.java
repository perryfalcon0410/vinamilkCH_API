package vn.viettel.sale.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.logging.LogMessage;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.ComboProductService;
import vn.viettel.sale.service.dto.ComboProductDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "API sản phẩm combo sử dụng cho xuất nhập combo")
public class ComboProductController extends BaseController {

    @Autowired
    ComboProductService comboProductService;
    private final String root = "/sales/combo-products";

    @GetMapping(value = { V1 + root })
    @ApiOperation(value = "Tìm kiếm sản phẩm combo trong xuất nhập combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<List<ComboProductDTO>> findComboProducts(HttpServletRequest request,
                                         @ApiParam("Tìm theo tên hoặc mã sản phẩm")
                                         @RequestParam(name = "keyWord", required = false, defaultValue = "") String keyWord,
                                         @ApiParam("Trạng thái hoạt động của sản phẩm")
                                         @RequestParam(name = "status", required = false) Integer status) {
        Response<List<ComboProductDTO>> response = comboProductService.findComboProducts(keyWord, status);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.FIND_COMBO_PRODUCTS_SUCCESS);
        return response;
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    @ApiOperation(value = "Chọn sản phẩm trong xuất nhập combo")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    public Response<ComboProductDTO> findComboProducts(HttpServletRequest request, @PathVariable Long id) {
        Response<ComboProductDTO> response = comboProductService.getComboProduct(id);
        LogFile.logToFile(appName, getUserName(), LogLevel.INFO, request, LogMessage.GET_COMBO_PRODUCT_SUCCESS);
        return response;
    }
}