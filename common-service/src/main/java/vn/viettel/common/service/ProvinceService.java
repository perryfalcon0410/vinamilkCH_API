package vn.viettel.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.common.service.dto.ProvinceDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface ProvinceService extends BaseService {

    Response<Page<ProvinceDTO>> index(String searchKeywords, Pageable pageable);

    Response<List<ProvinceDTO>> getAllProvinceByAreaIds(List<Long> areaIds);

    Response<List<ProvinceDTO>> getAllProvinceByIds(List<Long> ids);
}
