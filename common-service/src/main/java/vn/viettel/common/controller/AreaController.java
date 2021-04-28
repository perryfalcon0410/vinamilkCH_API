package vn.viettel.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.service.AreaService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;

import java.util.List;

@RestController
public class AreaController extends BaseController {
    @Autowired
    private AreaService areaService;
    private final String root = "/commons/areas";

    @GetMapping( value = { V1 + root + "/provinces", V2 + root + "/provinces"})
    public Response<List<AreaDTO>> getProvinces() {
        return areaService.getProvinces();
    }

    @GetMapping(value = { V1 + root + "/districts/index-customers"})
    public Response<List<AreaSearch>> getDistrictsToSearchCustomer() {
        return areaService.getDistrictsToSearchCustomer(this.getShopId());
    }

    @GetMapping(value = { V1 + root + "/districts"})
    public Response<List<AreaDTO>> getDistrictsByProvinceId(@RequestParam("provinceId") Long provinceId) {
        return areaService.getDistrictsByProvinceId(provinceId);
    }

    @GetMapping(value = { V1 + root + "/precincts"})
    public Response<List<AreaDTO>> getPrecinctsByDistrictId(@RequestParam("districtId")Long districtId) {
        return areaService.getPrecinctsByDistrictId(districtId);
    }

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<AreaDTO> getById(@PathVariable Long id) {
        return areaService.getAreaById(id);
    }
}
