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
        ShopDTO shopDTO = shopService.getById(id);
        return new Response<ShopDTO>().withData(shopDTO);
    }

    @ApiOperation(value = "Api dùng để lấy thông tin cửa hàng theo tên cửa hàng")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root})
    public Response<ShopDTO> getByName(@RequestParam String name) {
        ShopDTO shopDTO = shopService.getByName(name);
        return new Response<ShopDTO>().withData(shopDTO);
    }

    @ApiOperation(value = "Api dùng để lấy thông tin cửa hàng theo mã đơn vị")
    @ApiResponse(code = 200, message = "Success")
    @GetMapping(value = { V1 + root + "/code/{code}"})
    public Response<ShopDTO> getByShopCode(@PathVariable String code) {
        ShopDTO shopDTO = shopService.getByShopCode(code);
        return new Response<ShopDTO>().withData(shopDTO);
    }

    @RoleFeign
    @GetMapping(value = V1 + root + "/editable/online-order/{shopId}")
    public Response<Boolean> isEditableOnlineOrder(@PathVariable Long shopId) {
        Boolean response = shopService.isEditableOnlineOrder(shopId);
        return new Response<Boolean>().withData(response);
    }

    @RoleFeign
    @GetMapping(value = V1 + root + "/manually-creatable/online-order/{shopId}")
    Response<Boolean> isManuallyCreatableOnlineOrder(@PathVariable Long shopId) {
        Boolean response = shopService.isManuallyCreatableOnlineOrder(shopId);
        return new Response<Boolean>().withData(response);
    }

//    @RoleFeign
    @GetMapping(value = V1 + root + "/day-return/{shopId}")
    Response<String> dayReturn(@PathVariable Long shopId) {
        String string = shopService.dayReturn(shopId);
        return new Response<String>().withData(string);
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
    @GetMapping(value = V1 + root + "/import-trans-return/{shopId}")
    ShopParamDTO getImportTransReturn(@PathVariable Long shopId) {
        ShopParamDTO dto = shopService.getImportSaleReturn(shopId);
        return dto;
    }
}
