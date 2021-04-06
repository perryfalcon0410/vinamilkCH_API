package vn.viettel.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.customer.service.AreaService;

import java.util.List;

@RestController
@RequestMapping("/api/customer/area")
public class AreaController extends BaseController {
    @Autowired
    AreaService areaService;

    @GetMapping("/provinces")
    public Response<List<Area>> getProvinces() {
        return areaService.getProvinces();
    }

    @GetMapping("/districts")
    public Response<List<Area>> getDistrictsByProvinceId(@RequestParam("provinceId") Long provinceId) {
        return areaService.getDistrictsByProvinceId(provinceId);
    }

    @GetMapping("/precincts")
    public Response<List<Area>> getPrecinctsByDistrictId(@RequestParam("districtId")Long districtId) {
        return areaService.getPrecinctsByDistrictId(districtId);
    }
}
