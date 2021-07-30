package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.sale.messaging.ExchangeTransDetailRequest;
import vn.viettel.sale.messaging.ExchangeTransRequest;
import vn.viettel.sale.service.dto.ExchangeTotalDTO;
import vn.viettel.sale.service.dto.ExchangeTransDTO;

import java.util.Date;
import java.util.List;

public interface ExchangeTranService {
    Response<List<CategoryDataDTO>> getReasons();
    CoverResponse<Page<ExchangeTransDTO>, ExchangeTotalDTO> getAllExchange(Long roleId, Long shopId, String transCode, Date fromDate,
                                                                           Date toDate, Long reasonId, Pageable pageable);
    ExchangeTransDTO create(ExchangeTransRequest request, Long userId,Long shopId);
    ExchangeTransDTO update(Long id,ExchangeTransRequest request,Long shopId);
    ExchangeTransDTO getExchangeTrans(Long id);
    ExchangeTrans remove(Long id,Long shopId);
    List<ExchangeTransDetailRequest> getBrokenProducts(Long id);
}
