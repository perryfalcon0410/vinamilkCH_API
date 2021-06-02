package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.dto.CustomerTradeDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface CustomerNotTradeService {
    Object index(LocalDate fromDate, LocalDate toDate, Boolean isPaging, Pageable pageable);

    Page<CustomerTradeDTO>  findCustomerTrades(CustomerTradeFilter filter, Pageable pageable);

    ByteArrayInputStream customerTradesExportExcel(CustomerTradeFilter filter) throws IOException;

}
