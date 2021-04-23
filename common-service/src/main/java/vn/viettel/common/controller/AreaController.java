package vn.viettel.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.common.service.AreaService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;

import java.util.List;

@RestController
@RequestMapping("/api/common/areas")
public class AreaController extends BaseController {
    @Autowired
    AreaService areaService;

    @GetMapping("/provinces")
    public Response<List<AreaDTO>> getProvinces() {
        return areaService.getProvinces();
    }

    @GetMapping("/districts")
    public Response<List<AreaDTO>> getDistrictsByProvinceId(@RequestParam("provinceId") Long provinceId) {
        return areaService.getDistrictsByProvinceId(provinceId);
    }

    @GetMapping("/precincts")
    public Response<List<AreaDTO>> getPrecinctsByDistrictId(@RequestParam("districtId")Long districtId) {
        return areaService.getPrecinctsByDistrictId(districtId);
    }

    @GetMapping("/{id}")
    public Response<AreaDTO> getById(@PathVariable Long id) {
        return areaService.getAreaById(id);
    }

    @GetMapping("/precinct/{provinceId}")
    public Response<List<AreaDTO>> getPrecinctsByProvinceId(@PathVariable Long provinceId) {
        return areaService.getPrecinctsByProvinceId(provinceId);
    }
}
