package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.StockTotal;
import vn.viettel.core.repository.BaseRepository;

public interface StockTotalRepository extends BaseRepository<StockTotal> {
    StockTotal findByProductIdAndWareHouseTypeId(Long productId,Long wareHouseTypeId);
    @Query(value = "SELECT * FROM STOCK_TOTAL", nativeQuery = true)
    Page<StockTotal> findAll(Pageable pageable);
}
