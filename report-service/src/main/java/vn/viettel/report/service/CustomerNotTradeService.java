package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.dto.CustomerTradeDTO;

import java.util.Date;
import java.util.List;

public interface CustomerNotTradeService {
    Object index(Date fromDate, Date toDate, Boolean isPaging, Pageable pageable);

    List<Object[]> findCustomerTrades(CustomerTradeFilter filter, Pageable pageable);

}
