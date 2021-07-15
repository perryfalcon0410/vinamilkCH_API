package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.dto.CustomerNotTradePrintDTO;
import vn.viettel.report.service.dto.CustomerTradeDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public interface CustomerNotTradeService {
    Object index(Date fromDate, Date toDate, Boolean isPaging, Pageable pageable, Long shopId);

    /*
     * Danh sách khách hàng có giao dịch
     */
    Page<CustomerTradeDTO>  findCustomerTrades(CustomerTradeFilter filter, Pageable pageable);

    /*
     * Xuất excel khách hàng có giao dịch
     */
    ByteArrayInputStream customerTradesExportExcel(CustomerTradeFilter filter) throws IOException;
    CustomerNotTradePrintDTO printCustomerNotTrade(Date fromDate, Date toDate, Long shopId);

}
