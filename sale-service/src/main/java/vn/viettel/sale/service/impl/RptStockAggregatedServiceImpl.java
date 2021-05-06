package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.RptStockAggregated;
import vn.viettel.sale.repository.RptStockAggregatedRepository;
import vn.viettel.sale.service.RptStockAggregatedService;

import java.time.*;
import java.util.Date;

@Service
public class RptStockAggregatedServiceImpl extends BaseServiceImpl<RptStockAggregated, RptStockAggregatedRepository> implements RptStockAggregatedService {

    @Override
    public RptStockAggregated getRptStockAggregated(Long shopId, Long warehouseTypeId, Long productId, Date rptDate) {
        Date tDate = setTime(rptDate);
        RptStockAggregated rptStockAggregated = repository.getRptStockAggregated(shopId, warehouseTypeId, productId, tDate).orElse(null);
        return rptStockAggregated;
    }

    public Date setTime(Date date) {
        LocalDateTime localDateTime = LocalDateTime
                .of(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
