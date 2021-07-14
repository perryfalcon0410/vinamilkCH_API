package vn.viettel.sale.service;

import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.entities.StockTotal;

import java.util.List;

public interface StockTotalService extends BaseService {

    /*
    Lock record and unlock record
     */
    void lockUnLockRecord(List<StockTotal> entities, boolean isLock);

    void lockUnLockRecord(StockTotal entity, boolean isLock);
}
