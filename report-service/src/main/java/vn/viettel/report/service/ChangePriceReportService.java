package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.ChangePriceFilter;
import vn.viettel.report.service.dto.ChangePricePrintDTO;

import java.text.ParseException;
import java.time.LocalDateTime;

public interface ChangePriceReportService {
    Object index(ChangePriceFilter filter, Pageable pageable, Boolean isPaging) throws ParseException;
    ChangePricePrintDTO getAll(ChangePriceFilter filter, Pageable pageable) throws ParseException;
}
