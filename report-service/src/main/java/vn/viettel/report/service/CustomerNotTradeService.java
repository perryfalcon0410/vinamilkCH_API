package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.dto.CustomerNotTradePrintDTO;
import vn.viettel.report.service.dto.CustomerTradeDTO;
import vn.viettel.report.service.dto.CustomerTradeTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public interface CustomerNotTradeService {
    Object index(Date fromDate, Date toDate, Boolean isPaging, Pageable pageable, Long shopId);

    /*
     * Danh sách khách hàng có giao dịch
     */
    CoverResponse<Page<CustomerTradeDTO>, CustomerTradeTotalDTO> findCustomerTrades(CustomerTradeFilter filter, Pageable pageable);

    /*
     * Xuất excel khách hàng có giao dịch
     */
    ByteArrayInputStream customerTradesExportExcel(CustomerTradeFilter filter) throws IOException;

    /*
     * Danh sách khách hàng không có giao dịch
     */
    CustomerNotTradePrintDTO printCustomerNotTrade(Date fromDate, Date toDate, Long shopId);

}
