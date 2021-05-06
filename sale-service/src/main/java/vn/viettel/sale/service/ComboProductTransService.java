package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.dto.ComboProductTranDTO;

public interface ComboProductTransService extends BaseService {

    Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> findAll(ComboProductTranFilter filter, Pageable pageable);

    Response<ComboProductTranDTO> create(ComboProductTranRequest request, Long shopId, Long userId);

    Response<ComboProductTranDTO> getComboProductTrans(Long id);



}
