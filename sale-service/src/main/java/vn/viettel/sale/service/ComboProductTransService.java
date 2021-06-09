package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.messaging.ComboProductTranFilter;
import vn.viettel.sale.messaging.ComboProductTranRequest;
import vn.viettel.sale.service.dto.ComboProductTranDTO;
import vn.viettel.sale.service.dto.TotalDTO;

public interface ComboProductTransService extends BaseService {

    CoverResponse<Page<ComboProductTranDTO>, TotalDTO> getAll(ComboProductTranFilter filter, Pageable pageable);

    ComboProductTranDTO create(ComboProductTranRequest request, Long shopId, String userName);

    ComboProductTranDTO getComboProductTrans(Long id);

}
