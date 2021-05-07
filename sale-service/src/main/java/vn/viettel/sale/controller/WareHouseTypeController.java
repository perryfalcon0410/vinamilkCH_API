package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.sale.service.WareHouseTypeService;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;
import java.util.List;

@RestController
public class WareHouseTypeController extends BaseController {

    @Autowired
    WareHouseTypeService wareHouseTypeService;
    private final String root = "/sales/warehouse";

    @RoleAdmin
    @GetMapping(value = V1+root)
    public Response<List<WareHouseTypeDTO>> index() {
        return wareHouseTypeService.index(this.getShopId());
    }
}
