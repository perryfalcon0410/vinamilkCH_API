package vn.viettel.sale.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.WareHouseTypeService;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;
import java.util.List;

@RestController
@Api(tags = "Lấy danh sách loại kho")
public class WareHouseTypeController extends BaseController {

    @Autowired
    WareHouseTypeService wareHouseTypeService;
    private final String root = "/sales/warehouse";

    @GetMapping(value = V1+root)
    @ApiOperation(value = "Lấy danh sách loại khô")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )

    public Response<List<WareHouseTypeDTO>> index() {
        return wareHouseTypeService.index(this.getShopId());
    }
}
