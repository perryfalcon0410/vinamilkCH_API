package vn.viettel.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.common.service.dto.AreaDTO;
import vn.viettel.common.service.dto.DistrictDTO;
import vn.viettel.common.service.dto.ProvinceDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface DistrictService extends BaseService {

    Response<Page<DistrictDTO>> index(String searchKeywords, Pageable pageable);

    Response<List<ProvinceDTO>> getAllDistrictByAreaIds(List<Long> provinceIds);
}
