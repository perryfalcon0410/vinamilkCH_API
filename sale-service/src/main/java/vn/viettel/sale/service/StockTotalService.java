package vn.viettel.sale.service;

import vn.viettel.core.service.BaseService;
import vn.viettel.sale.entities.StockTotal;

import java.util.HashMap;
import java.util.List;

public interface StockTotalService extends BaseService {

    /*
    Lock record and unlock record
     */
    void updateWithLock(HashMap<Long,Integer> idAndValues);

    StockTotal updateWithLock(Long shopId, Long wareHouseId, Long productId, Integer value);

    /**With @type=2, block creates record in stock total on delete**/
    StockTotal updateWithLock(Long shopId, Long wareHouseId, Long productId, Integer value,Integer type);

    StockTotal createStockTotal(Long shopId, Long wareHouseId, Long productId, Integer value, boolean autoSave);

    void validateStockTotal(List<StockTotal> stockTotals, Long productId, Integer value);

    void showMessage(Long productId, boolean notFound);
}
