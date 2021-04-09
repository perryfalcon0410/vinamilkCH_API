package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.db.entity.stock.ExchangeTrans;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

import java.util.Date;
import java.util.List;

public interface ExchangeTranService {
    Response<List<CategoryData>> getReasons();
    Response<Page<ExchangeTransDTO>> getAllExchange(Long roleId, Long shopId, Long formId,
                                                    Long ctrlId, String transCode, Date fromDate,
                                                    Date toDate, Long reasonId, Pageable pageable);
    Response<ExchangeTrans> create(ExchangeTransRequest request, Long userId);
}
