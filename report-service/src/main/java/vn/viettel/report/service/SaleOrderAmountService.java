package vn.viettel.report.service;

import org.springframework.data.domain.Pageable;
import vn.viettel.report.messaging.SaleOrderAmountFilter;
import vn.viettel.report.service.dto.TableDynamicDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface SaleOrderAmountService {
    /*
     * Xuất excel báo cáo khách hàng theo doanh số
     */
    ByteArrayInputStream exportExcel(SaleOrderAmountFilter filter) throws IOException;

    /*
     * Danh sách báo cáo khách hàng theo doanh số
     */
    TableDynamicDTO findAmounts(SaleOrderAmountFilter filter, Pageable pageable) ;

}
