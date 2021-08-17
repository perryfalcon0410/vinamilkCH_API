package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.service.dto.ChangePriceDTO;
import vn.viettel.report.service.dto.ChangePricePrintDTO;
import vn.viettel.report.service.dto.ChangePriceTotalDTO;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ChangePriceReportService {
    Object index(String searchKey, Long shopId, LocalDateTime fromTransDate, LocalDateTime toTransDate, LocalDateTime fromOrderDate,
                 LocalDateTime toOrderDate, String ids, Pageable pageable, Boolean isPaging) throws ParseException;
    ChangePricePrintDTO getAll(String searchKey,  Long shopId, LocalDateTime fromTransDate, LocalDateTime toTransDate, LocalDateTime fromOrderDate,
                               LocalDateTime toOrderDate, String ids, Pageable pageable) throws ParseException;
}
