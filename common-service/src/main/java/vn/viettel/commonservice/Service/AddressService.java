package vn.viettel.commonservice.Service;

import vn.viettel.commonservice.Service.dto.ProDisDto;
import vn.viettel.core.db.entity.Ward;
import vn.viettel.core.messaging.Response;

import java.util.List;

public interface AddressService {
    String getCountryById(long id);
    String getProvinceById(long id);
    String getDistrictById(long id);
    String getWardById(long id);
    String getAddressById(long id);
    Response<List<ProDisDto>> getDistrictByProId(long id);
    Response<List<Ward>> getWardByDistrictId(long id);
}
