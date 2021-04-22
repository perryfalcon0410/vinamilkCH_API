package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.dto.ComboProductTranDTO;

import javax.validation.Valid;

public interface ComboProductTransService extends BaseService {

    Response<CoverResponse<Page<ComboProductTranDTO>, TotalResponse>> findAll(ComboProductTranFilter filter, Pageable pageable);

    Response<ComboProductTranDTO> create(ComboProductTranRequest request, Long shopId);

    Response<ComboProductTranDTO> getComboProductTrans(Long id);



}
