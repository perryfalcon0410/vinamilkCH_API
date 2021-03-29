package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface SaleOrderDetailRepository extends BaseRepository<SaleOrderDetail> {
    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL", nativeQuery = true)
    List<SaleOrderDetail> getListSaleOrderDetail();

    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL WHERE SALE_ORDER_ID = :ID", nativeQuery = true)
    List<SaleOrderDetail> getBySaleOrderId(Long ID);

    @Query(value = "SELECT PRODUCT_ID FROM SALE_ORDER_DETAIL " +
        "WHERE SHOP_ID = :shopId " +
        "GROUP BY PRODUCT_ID " +
        "ORDER BY SUM(QUANTITY) DESC", nativeQuery = true
    )
    Page<BigDecimal> findProductTopSale(Long shopId, Pageable pageable);

}
