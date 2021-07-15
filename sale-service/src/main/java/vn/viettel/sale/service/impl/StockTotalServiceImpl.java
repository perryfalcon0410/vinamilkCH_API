package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.StockTotalService;

import javax.persistence.LockModeType;
import java.util.List;

@Service
public class StockTotalServiceImpl extends BaseServiceImpl<StockTotal, StockTotalRepository> implements StockTotalService {

    @Override
    public void lockUnLockRecord(List<StockTotal> entities, boolean isLock) {
        if(entities == null || entities.isEmpty()) return ;
        for (Object entity : entities.toArray()){
            if (isLock) {
                entityManager.lock(entity, LockModeType.PESSIMISTIC_READ);
                entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
            }
            else entityManager.lock(entity, LockModeType.NONE);
        }
    }

    @Override
    public void lockUnLockRecord(StockTotal entity, boolean isLock) {
        if(entity == null) return;
        if (isLock) {
            entityManager.lock(entity, LockModeType.PESSIMISTIC_READ);
            entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
        }
        else entityManager.lock(entity, LockModeType.NONE);
    }
}
