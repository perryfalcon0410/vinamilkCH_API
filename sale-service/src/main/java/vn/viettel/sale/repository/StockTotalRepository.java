package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.StockTotal;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface StockTotalRepository extends BaseRepository<StockTotal> {
    StockTotal findByProductIdAndWareHouseTypeId(Long productId,Long wareHouseTypeId);
    @Query(value = "SELECT * FROM STOCK_TOTAL", nativeQuery = true)
    Page<StockTotal> findAll(Pageable pageable);


    @Query(value = "SELECT * FROM STOCK_TOTAL WHERE SHOP_ID =:shopId " +
            "AND WARE_HOUSE_TYPE_ID =:wareHouseTypeId AND PRODUCT_ID =:productId AND STATUS = 1 AND DELETED_AT IS NULL", nativeQuery = true)
    Optional<StockTotal> getStockTotal(Long shopId, Long wareHouseTypeId, Long productId);

}
