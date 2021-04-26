package vn.viettel.common.service;

import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface AreaService extends BaseService {
    Response<AreaDTO> getAreaById(Long id);
    Response<List<AreaDTO>> getProvinces();
    Response<List<AreaDTO>> getDistrictsByProvinceId(Long provinceId);
    Response<List<AreaDTO>> getPrecinctsByDistrictId(Long districtId);
    Response<List<AreaSearch>> getDistrictsToSearchCustomer(Long shopId);

}