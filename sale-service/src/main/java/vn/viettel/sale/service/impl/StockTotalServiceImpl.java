package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.StockTotalService;

import javax.persistence.LockModeType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockTotalServiceImpl extends BaseServiceImpl<StockTotal, StockTotalRepository> implements StockTotalService {

    private Map<String, Object> properties = new HashMap<String, Object>() {{
        put("javax.persistence.lock.timeout", 1000);
    }};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithLock(HashMap<StockTotal,Integer> idAndValues){
        if(idAndValues == null) return;
        for(Map.Entry<StockTotal, Integer> entry : idAndValues.entrySet()){
            updateWithLock(entry.getKey(),entry.getValue() );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithLock(StockTotal entity, Integer value){
        if(entity == null || value == null) return;
//        for (int i = 0; ; i++) {
//            if (entityManager.getLockMode(entity) == LockModeType.PESSIMISTIC_FORCE_INCREMENT ||
//                    entityManager.getLockMode(entity) == LockModeType.PESSIMISTIC_READ ||
//                    entityManager.getLockMode(entity) == LockModeType.PESSIMISTIC_WRITE){
//                continue;
//            }else{
//                break;
//            }
//        }
        StockTotal newEntity = repository.findById(entity.getId()).get();
        if (newEntity == null) return;
        updateEntity(entity, value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockTotal updateWithLock(Long shopId, Long wareHouseId, Long productId, Integer value){
        if(shopId == null || wareHouseId == null || productId == null || value == null) return null;
        StockTotal entity = repository.findByProductIdAndWareHouseTypeIdAndShopId(productId, wareHouseId,shopId);
        if (entity == null) return null;
//        boolean reload = false;

//        for (int i = 0; ; i++) {
//            if (entityManager.getLockMode(entity) == LockModeType.PESSIMISTIC_FORCE_INCREMENT ||
//                    entityManager.getLockMode(entity) == LockModeType.PESSIMISTIC_READ ||
//                    entityManager.getLockMode(entity) == LockModeType.PESSIMISTIC_WRITE){
//                reload = true;
//                continue;
//            }else {
//                break;
//            }
//        }
//        if(reload) entity = repository.findByProductIdAndWareHouseTypeIdAndShopId(productId, wareHouseId,shopId);
        return updateEntity(entity, value);
    }

    @Transactional(rollbackFor = Exception.class)
    public StockTotal updateEntity(StockTotal entity, Integer value){
        entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE, properties);
        if(entity.getQuantity() == null) entity.setQuantity(0);
        entity.setQuantity(entity.getQuantity() + value);
        if(entity.getQuantity() < 0) throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE);
        repository.save(entity);
        entityManager.lock(entity, LockModeType.NONE);
        return entity;
    }
}
