package vn.viettel.sale.service;

import vn.viettel.core.service.BaseService;
import vn.viettel.sale.entities.RptStockAggregated;

import java.util.Date;

public interface RptStockAggregatedService extends BaseService {
    RptStockAggregated getRptStockAggregated(Long shopId, Long warehouseTypeId, Long productId, Date rptDate);
}
