package service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import service.SaleOrderReturnService;
import service.dto.SaleOrderDetailRs;
import vn.viettel.core.messaging.Response;

import java.util.Date;

public class SaleOrderReturnServiceImpl implements SaleOrderReturnService {

    @Override
    public Response<Page<SaleOrderDetailRs>> find(String stockCountingCode, Date fromDate, Date toDate, Pageable pageable) {
        return null;
    }
}
