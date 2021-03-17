package vn.viettel.commonservice.service;

import vn.viettel.commonservice.service.dto.CreateAddressDto;
import vn.viettel.commonservice.service.dto.CreateAddressResponse;
import vn.viettel.commonservice.service.dto.LocationResponse;
import vn.viettel.commonservice.service.dto.ProDisDto;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface AddressService {
    Response<List<LocationResponse>> getAllCountry();
    Response<List<LocationResponse>> getAllProvince();
    Response<List<LocationResponse>> getAllDistrict();
    Response<List<LocationResponse>> getAllWard();
    Response<List<LocationResponse>> getAllAddress();
    Response<List<LocationResponse>> getDistrictByProvinceId(long id);
    Response<List<LocationResponse>> getWardByDistrict(long id);
    String getCountryById(long id);
    String getProvinceById(long id);
    String getDistrictById(long id);
    String getWardById(long id);
    String getAddressById(long id);
    Response<List<ProDisDto>> getDistrictByProId(long id);
    Response<List<Ward>> getWardByDistrictId(long id);
    CreateAddressResponse createAddress(CreateAddressDto createAddressDto);
}
