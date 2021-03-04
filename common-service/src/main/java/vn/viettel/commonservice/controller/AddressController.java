package vn.viettel.commonservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.commonservice.Service.AddressService;
import vn.viettel.commonservice.Service.dto.ProDisDto;
import vn.viettel.core.db.entity.Ward;
import vn.viettel.core.messaging.Response;

import java.util.List;

@RestController
@RequestMapping("api/location")
public class AddressController {
    @Autowired
    AddressService service;

    @GetMapping("/country/{id}")
    public String getCountry(@PathVariable long id) {
        return service.getCountryById(id);
    }

    @GetMapping("/province/{id}")
    public String getProvince(@PathVariable long id) {
        return service.getProvinceById(id);
    }

    @GetMapping("/district/{id}")
    public String getDistrict(@PathVariable long id) {
        return service.getDistrictById(id);
    }

    @GetMapping("/ward/{id}")
    public String getWard(@PathVariable long id) {
        return service.getWardById(id);
    }

    @GetMapping("/address/{id}")
    public String getAddress(@PathVariable long id) {
        return service.getAddressById(id);
    }

    @GetMapping("/province-district/{proId}")
    public Response<List<ProDisDto>> getDistrictByProId(@PathVariable long proId) {
        return service.getDistrictByProId(proId);
    }

    @GetMapping("/ward-district/{disId}")
    public Response<List<Ward>> getWardByDistrictId(@PathVariable long disId) {
        return service.getWardByDistrictId(disId);
    }
}
