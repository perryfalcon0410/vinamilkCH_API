package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.dto.ExchangeProductDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

import java.util.Date;
import java.util.List;

public interface ExchangeTranService {
    Response<List<CategoryDataDTO>> getReasons();
    Response<Page<ExchangeTransDTO>> getAllExchange(Long roleId, Long shopId, String transCode, Date fromDate,
                                                    Date toDate, Long reasonId, Pageable pageable);
    Response<ExchangeTrans> create(ExchangeTransRequest request, Long userId);

    Response<List<ExchangeProductDTO>> getBrokenProducts(Long id);
}
