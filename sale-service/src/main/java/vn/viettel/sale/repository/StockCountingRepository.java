package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockCountingRepository extends BaseRepository<StockCounting>, JpaSpecificationExecutor<StockCounting> {

    @Query(value = "SELECT s FROM StockCounting s WHERE s.wareHouseTypeId = :warehouseTypeId AND s.shopId = :shopId AND " +
            " s.countingDate >= :startDate AND s.countingDate <= :endDate ")
    List<StockCounting> findByWareHouseTypeId(Long warehouseTypeId, Long shopId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT s FROM StockCounting s WHERE s.shopId =:shopId " +
            " And s.createdAt>= :startDate " +
            " ORDER BY s.stockCountingCode desc ")
    Page<StockCounting> getLastStockCounting(Long shopId, LocalDateTime startDate, Pageable pageable);
}
