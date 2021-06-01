package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;


public interface StockTotalRepository extends BaseRepository<StockTotal> {
    StockTotal findByProductIdAndWareHouseTypeId(Long productId,Long wareHouseTypeId);
    @Query(value = "SELECT * FROM STOCK_TOTAL s JOIN PRODUCTS p ON s.PRODUCT_ID = p.ID WHERE p.STATUS = 1 AND s.WARE_HOUSE_TYPE_ID = :wareHouseType " +
            "ORDER BY s.PRODUCT_ID asc", nativeQuery = true)
    Page<StockTotal> findAll(Pageable pageable, Long wareHouseType);

    @Query(value = "SELECT * FROM STOCK_TOTAL WHERE SHOP_ID =:shopId " +
            "AND WARE_HOUSE_TYPE_ID =:wareHouseTypeId AND PRODUCT_ID =:productId AND STATUS = 1 ", nativeQuery = true)
    Optional<StockTotal> getStockTotal(Long shopId, Long wareHouseTypeId, Long productId);

}
