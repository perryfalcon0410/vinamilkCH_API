package vn.viettel.commonservice.Service;

import vn.viettel.commonservice.Service.dto.CreateAddressDto;
import vn.viettel.commonservice.Service.dto.LocationResponse;
import vn.viettel.commonservice.Service.dto.ProDisDto;
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
    Response<Address> createAddress(CreateAddressDto createAddressDto);
}
