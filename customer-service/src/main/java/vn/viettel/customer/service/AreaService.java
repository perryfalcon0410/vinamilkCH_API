package vn.viettel.customer.service;

import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface AreaService extends BaseService {
    Response<List<AreaDTO>> getAll();
    Response<AreaDTO> getAreaById(Long id);
    Response<List<AreaDTO>> getProvinces();
    Response<List<AreaDTO>> getDistrictsByProvinceId(Long provinceId);
    Response<List<AreaDTO>> getPrecinctsByDistrictId(Long districtId);
    Response<List<AreaDTO>> getPrecinctsByProvinceId(Long provinceId);
}
