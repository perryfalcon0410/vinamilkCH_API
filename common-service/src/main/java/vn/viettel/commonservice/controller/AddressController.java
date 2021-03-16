package vn.viettel.commonservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.commonservice.service.AddressService;
import vn.viettel.commonservice.service.dto.CreateAddressDto;
import vn.viettel.commonservice.service.dto.LocationResponse;
import vn.viettel.commonservice.service.dto.ProDisDto;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;

import java.util.List;

@RestController
@RequestMapping("api/location")
public class AddressController {
    @Autowired
    AddressService service;

    @GetMapping("/countries")
    public Response<List<LocationResponse>> getAllCountry() {
        return service.getAllCountry();
    }

    @GetMapping("/provinces")
    public Response<List<LocationResponse>> getAllProvince() {
        return service.getAllProvince();
    }

    @GetMapping("/districts")
    public Response<List<LocationResponse>> getAllDistrict() {
        return service.getAllDistrict();
    }

    @GetMapping("/wards")
    public Response<List<LocationResponse>> getAllWard() {
        return service.getAllWard();
    }

    @GetMapping("/address")
    public Response<List<LocationResponse>> getAllAddress() {
        return service.getAllAddress();
    }

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

    @GetMapping("/district-by-province/{proId}")
    public Response<List<LocationResponse>> getDistrictByProvinceID(@PathVariable long proId) {
        return service.getDistrictByProvinceId(proId);
    }

    @GetMapping("/ward-by-district/{disId}")
    public Response<List<LocationResponse>> getWardByDistrict(@PathVariable long disId) {
        return service.getWardByDistrict(disId);
    }

    @PostMapping("create-address")
    public Response<Address> createAddress(@RequestBody CreateAddressDto addressDto) {
        return service.createAddress(addressDto);
    }
}
