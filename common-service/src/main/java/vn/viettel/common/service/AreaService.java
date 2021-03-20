package vn.viettel.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.common.service.dto.AreaDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface AreaService extends BaseService {

    Response<Page<AreaDTO>> index(String searchKeywords, Pageable pageable);

    Response<List<AreaDTO>> getAllAreaByCountryIds(List<Long> countryId);
}
