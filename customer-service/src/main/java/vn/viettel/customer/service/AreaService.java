package vn.viettel.customer.service;

import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface AreaService extends BaseService {
    Response<List<Area>> getAll();
    Response<Area> getAreaById(Long id);
    Response<List<Area>> getProvinces();
    Response<List<Area>> getDistrictsByProvinceId(Long provinceId);
    Response<List<Area>> getPrecinctsByDistrictId(Long districtId);
    Response<List<Area>> getPrecinctsByProvinceId(Long provinceId);
}
