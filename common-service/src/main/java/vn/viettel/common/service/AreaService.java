package vn.viettel.common.service;

import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.service.dto.AreaDefaultDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface AreaService extends BaseService {
    AreaDTO getAreaById(Long id);
    List<AreaDTO> getProvinces();
    List<AreaDTO> getDistrictsByProvinceId(Long provinceId);
    List<AreaDTO> getPrecinctsByDistrictId(Long districtId);
    List<AreaSearch> getDistrictsToSearchCustomer(Long shopId);
    AreaDTO getArea(String provinceName, String districtName, String precinctName);
    AreaDefaultDTO getAreaDefault(Long shopId);

}
