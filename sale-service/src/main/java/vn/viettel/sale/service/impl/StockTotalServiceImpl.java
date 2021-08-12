package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.repository.ProductRepository;
import vn.viettel.sale.repository.StockTotalRepository;
import vn.viettel.sale.service.StockTotalService;

import javax.persistence.LockModeType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StockTotalServiceImpl extends BaseServiceImpl<StockTotal, StockTotalRepository> implements StockTotalService {

    @Autowired
    ProductRepository productRepository;

    private Map<String, Object> properties = new HashMap<String, Object>() {{
        put("javax.persistence.lock.timeout", 500);
    }};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithLock(HashMap<Long,Integer> idAndValues){
        if(idAndValues == null) return;
        for(Map.Entry<Long, Integer> entry : idAndValues.entrySet()){
            updateWithLock(entry.getKey(),entry.getValue() );
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateWithLock(Long stockTotalId, Integer value){
        if(stockTotalId == null || value == null) return;
        StockTotal newEntity = repository.findById(stockTotalId).get();
        entityManager.refresh(newEntity);
        if (newEntity == null) return;
        updateEntity(newEntity, value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockTotal updateWithLock(Long shopId, Long wareHouseId, Long productId, Integer value){
        if(shopId == null || wareHouseId == null || productId == null || value == null) return null;
        StockTotal entity = repository.findByProductIdAndWareHouseTypeIdAndShopId(productId, wareHouseId,shopId);
        if (entity == null && value > 0) return createStockTotal(shopId, wareHouseId, productId, value, true);
        if(entity == null && value < 0) showMessage(productId, true);
        entityManager.refresh(entity);
        return updateEntity(entity, value);
    }

    @Override
    public StockTotal updateWithLock(Long shopId, Long wareHouseId, Long productId, Integer value, Integer type) {
        if(shopId == null || wareHouseId == null || productId == null || value == null) return null;
        StockTotal entity = repository.findByProductIdAndWareHouseTypeIdAndShopId(productId, wareHouseId,shopId);
        if(entity==null){
            if(type != null && type == 2) {
                showMessage(productId, true);
            }else {
                if (entity == null && value > 0) return createStockTotal(shopId, wareHouseId, productId, value, true);
                if(entity == null && value < 0) showMessage(productId, true);
            }
        }
        entityManager.refresh(entity);
        return updateEntity(entity, value);
    }

    @Transactional(rollbackFor = Exception.class)
    public StockTotal createStockTotal(Long shopId, Long wareHouseId, Long productId, Integer value, boolean autoSave){
        if(shopId == null || productId == null || value == null) return null;
        if(wareHouseId == null) throw new ValidateException(ResponseMessage.WARE_HOUSE_NOT_EXIST);
        StockTotal newStockTotal = new StockTotal();
        newStockTotal.setProductId(productId);
        newStockTotal.setQuantity(value);
        newStockTotal.setWareHouseTypeId(wareHouseId);
        newStockTotal.setShopId(shopId);
        newStockTotal.setStatus(1);
        if(autoSave) repository.save(newStockTotal);

        return newStockTotal;
    }

    @Transactional(rollbackFor = Exception.class)
    public StockTotal updateEntity(StockTotal entity, Integer value){
        entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE, properties);
        if(entity.getQuantity() == null) entity.setQuantity(0);
        entity.setQuantity(entity.getQuantity() + value);
        if(entity.getQuantity() < 0) showMessage(entity.getProductId(), false);
        repository.save(entity);
        repository.flush();
        entityManager.lock(entity, LockModeType.NONE);
        return entity;
    }

    public void validateStockTotal(List<StockTotal> stockTotals, Long productId, Integer value){
        if(productId == null) return;
        boolean flag = false;
        for (StockTotal stockTotal : stockTotals){
            if(stockTotal.getProductId().equals(productId)){
                entityManager.refresh(stockTotal);
                if(stockTotal.getQuantity() == null) stockTotal.setQuantity(0);
                flag = true;
                if(value != null && value < 0 && stockTotal.getQuantity() + value < 0) {
                    showMessage(productId, false);
                }
                break;
            }
        }
        if(flag == false) {
            showMessage(productId, true);
        }
    }

    public void showMessage(Long productId, boolean notFound){
        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent()) throw new ValidateException(ResponseMessage.PRODUCT_DOES_NOT_EXISTS);
        String name = product.get().getProductCode() + " - " + product.get().getProductName();
        if(notFound)
            throw new ValidateException(ResponseMessage.PRODUCT_STOCK_TOTAL_NOT_FOUND, name);
        else
            throw new ValidateException(ResponseMessage.STOCK_TOTAL_CANNOT_BE_NEGATIVE_SSS, name);
    }
}
