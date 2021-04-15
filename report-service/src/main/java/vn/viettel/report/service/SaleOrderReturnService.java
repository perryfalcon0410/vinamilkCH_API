package vn.viettel.report.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.dto.SaleOrderDetailRs;

import java.util.Date;

public interface SaleOrderReturnService {
    Response<Page<SaleOrderDetailRs>> find(String stockCountingCode,
                                           Date fromDate,
                                           Date toDate, Pageable pageable);
}
