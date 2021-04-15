package vn.viettel.report.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.report.service.SaleOrderReturnService;
import vn.viettel.report.service.dto.SaleOrderDetailRs;

import java.util.Date;

@Service
public class SaleOrderReturnServiceImpl implements SaleOrderReturnService {

    @Override
    public Response<Page<SaleOrderDetailRs>> find(String stockCountingCode, Date fromDate, Date toDate, Pageable pageable) {
        return null;
    }
}
