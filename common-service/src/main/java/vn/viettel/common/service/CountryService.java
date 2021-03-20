package vn.viettel.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.common.service.dto.CountryDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

public interface CountryService extends BaseService {

    Response<Page<CountryDTO>> index(String searchKeywords, Pageable pageable);
}
