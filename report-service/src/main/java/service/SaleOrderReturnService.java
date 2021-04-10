package service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import service.dto.SaleOrderDetailRs;
import vn.viettel.core.messaging.Response;

import java.util.Date;

public interface SaleOrderReturnService {
    Response<Page<SaleOrderDetailRs>> find(String stockCountingCode,
                                           Date fromDate,
                                           Date toDate, Pageable pageable);
}
