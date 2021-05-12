package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface ChangePriceReportService {
    Response<CoverResponse<Page<ChangePriceDTO>, ChangePriceTotalDTO>> index(String searchKey, Date fromTransDate, Date toTransDate, Date fromOrderDate,
                                                                             Date toOrderDate, String ids, Pageable pageable) throws ParseException;
    Response<List<CoverResponse<ChangePriceTotalDTO, List<ChangePriceDTO>>>> getAll(String searchKey, Date fromTransDate, Date toTransDate, Date fromOrderDate,
                                                                 Date toOrderDate, String ids, Pageable pageable) throws ParseException;
}
