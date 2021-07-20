package vn.viettel.sale.service;

import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.entities.StockTotal;

import java.util.HashMap;
import java.util.List;

public interface StockTotalService extends BaseService {

    /*
    Lock record and unlock record
     */
    void updateWithLock(HashMap<StockTotal,Integer> idAndValues);

    void updateWithLock(StockTotal entity, Integer value);

    StockTotal updateWithLock(Long shopId, Long wareHouseId, Long productId, Integer value);
}
