package vn.viettel.authorization.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.ShopParamRequest;
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

    @RoleFeign
    @GetMapping(value = V1 + root + "/shop-params")
    Response<ShopParamDTO> getShopParam(@RequestParam String type, @RequestParam String code, @RequestParam Long shopId ) {
        ShopParamDTO dto = shopService.getShopParam(type, code, shopId );
        return new Response<ShopParamDTO>().withData(dto);
    }

    @RoleFeign
    @PutMapping(value = V1 + root + "/shop-params-1/{id}")
    Response<ShopParamDTO> updateShopParam(@RequestBody ShopParamRequest request, @PathVariable Long id) {
        ShopParamDTO dto = shopService.updateShopParam(request, id);
        return new Response<ShopParamDTO>().withData(dto);
    }
}
