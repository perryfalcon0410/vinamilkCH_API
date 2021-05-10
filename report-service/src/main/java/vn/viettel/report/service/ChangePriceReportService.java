package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;

import java.text.ParseException;

public interface ChangePriceReportService {
    Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>> index(String searchKey, String fromTransDate, String toTransDate, String fromOrderDate,
                                                                             String toOrderDate, String ids, Pageable pageable) throws ParseException;
}
