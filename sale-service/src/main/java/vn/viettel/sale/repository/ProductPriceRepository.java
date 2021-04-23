package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.Price;
import vn.viettel.core.repository.BaseRepository;

import java.util.Date;

public interface ProductPriceRepository extends BaseRepository<Price> {

    @Query(
        value = "SELECT * FROM PRICES WHERE PRODUCT_ID =:productId AND CUSTOMER_TYPE_ID =:customerTypeId " +
            "AND STATUS = 1 AND PRICE_TYPE = -1 "
    , nativeQuery = true)
    Price getProductPrice(Long productId, Long customerTypeId);

    @Query(value = "SELECT * FROM PRICES WHERE PRODUCT_ID = :productId " +
            "AND sysdate BETWEEN FROM_DATE AND TO_DATE", nativeQuery = true)
    Price findByProductId(Long productId);

    @Query(value = "SELECT  to_date(max(from_date), 'DD-MM-YYYY') " +
            "FROM prices " +
            "WHERE product_id = :productId and from_date < :orderDate ORDER By from_date DESC", nativeQuery = true)
    Date findByProductIdAndOrderDate(Long productId, Date orderDate);

    @Query(value = "SELECT * FROM prices WHERE TRUNC(from_date) = TO_DATE(:date ,'yy-MM-dd')", nativeQuery = true)
    Price findByFromDate(String date);
}
