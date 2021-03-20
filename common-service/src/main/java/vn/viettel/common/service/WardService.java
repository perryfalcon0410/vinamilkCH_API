package vn.viettel.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.common.service.dto.WardDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface WardService extends BaseService {

    Response<Page<WardDTO>> index(String searchKeywords, Pageable pageable);

    Response<List<WardDTO>> getAllWardByDistrictIds(List<Long> provinceIds);
}
