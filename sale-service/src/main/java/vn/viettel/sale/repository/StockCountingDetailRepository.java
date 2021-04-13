package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.StockCountingDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface StockCountingDetailRepository extends BaseRepository<StockCountingDetail>, JpaSpecificationExecutor<StockCountingDetail> {
    List<StockCountingDetail> findByStockCountingId(Long id);
    @Query(value = "SELECT * FROM STOCK_COUNTING_DETAIL WHERE STOCK_COUNTING_ID = :id", nativeQuery = true)
    Page<StockCountingDetail> findByStockCountingId(Long id, Pageable pageable);
}
