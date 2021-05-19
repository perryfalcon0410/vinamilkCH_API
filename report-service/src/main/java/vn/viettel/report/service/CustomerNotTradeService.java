package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface CustomerNotTradeService {
    Object index(Date fromDate, Date toDate, Boolean isPaging, Pageable pageable);
}
