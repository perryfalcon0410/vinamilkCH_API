package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrderDiscount;

import java.util.List;

public interface SaleOrderDiscountRepository extends BaseRepository<SaleOrderDiscount> {

    @Query(value = "SELECT COUNT(*) FROM ( SELECT s.id, s.ORDER_DATE FROM  SALE_ORDERS s " +
        "        JOIN SALE_ORDER_DISCOUNT d ON s.id = d.SALE_ORDER_ID " +
        "     WHERE s.SHOP_ID =:shopId AND s.CUSTOMER_ID =:customerId AND TRUNC(s.ORDER_DATE) = TRUNC(sysdate) " +
        "     GROUP BY s.id, s.ORDER_DATE )", nativeQuery = true)
    Integer countDiscount(Long shopId, Long customerId );


    List<SaleOrderDiscount> findAllBySaleOrderIdAndProductId(Long id, Long productId);
}
