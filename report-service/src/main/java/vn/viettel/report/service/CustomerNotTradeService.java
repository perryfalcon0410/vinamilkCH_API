package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.CoverResponse;
import vn.viettel.report.messaging.CustomerNotTradeFilter;
import vn.viettel.report.messaging.CustomerTradeFilter;
import vn.viettel.report.service.dto.CustomerNotTradePrintDTO;
import vn.viettel.report.service.dto.CustomerTradeDTO;
import vn.viettel.report.service.dto.CustomerTradeTotalDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface CustomerNotTradeService {
    Object index(CustomerNotTradeFilter filter, Boolean isPaging, Pageable pageable);
    /*
     * Danh sách khách hàng không có giao dịch
     */
    CustomerNotTradePrintDTO printCustomerNotTrade(CustomerNotTradeFilter filter);

    /*
     * Danh sách khách hàng có giao dịch
     */
    CoverResponse<Page<CustomerTradeDTO>, CustomerTradeTotalDTO> findCustomerTrades(CustomerTradeFilter filter, Pageable pageable);

    /*
     * Xuất excel khách hàng có giao dịch
     */
    ByteArrayInputStream customerTradesExportExcel(CustomerTradeFilter filter) throws IOException;



}
