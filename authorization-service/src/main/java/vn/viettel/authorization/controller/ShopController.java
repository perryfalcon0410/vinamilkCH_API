package vn.viettel.authorization.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleFeign;

@RestController
@Api(tags = "Api sử dụng cho lấy thông tin cửa hàng")
public class ShopController extends BaseController {
    @Autowired
    ShopService shopService;

    private final String root = "/users/shops";

    @ApiOperation(value = "Api dùng để lấy thông tin cửa hàng theo id")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ShopDTO> getById(@PathVariable Long id) {
        return shopService.getById(id);
    }

    @ApiOperation(value = "Api dùng để lấy thông tin cửa hàng theo tên cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root})
    public Response<ShopDTO> getByName(@RequestParam String name) {
        return shopService.getByName(name);
    }

    @RoleFeign
    @GetMapping(value = V1 + root + "/editable/online-order/{shopId}")
    public Response<Boolean> isEditableOnlineOrder(@PathVariable Long shopId) {
        return shopService.isEditableOnlineOrder(shopId);
    }

    @RoleFeign
    @GetMapping(value = V1 + root + "/manually-creatable/online-order/{shopId}")
    Response<Boolean> isManuallyCreatableOnlineOrder(@PathVariable Long shopId) {
        return shopService.isManuallyCreatableOnlineOrder(shopId);
    }

//    @RoleFeign
    @GetMapping(value = V1 + root + "/day-return/{shopId}")
    Response<String> dayReturn(@PathVariable Long shopId) {
        return shopService.dayReturn(shopId);
    }
}
