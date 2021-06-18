package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.Price;
import vn.viettel.core.repository.BaseRepository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductPriceRepository extends BaseRepository<Price> {

//    @Query(
//            value = "SELECT * FROM PRICES WHERE PRODUCT_ID =:productId AND CUSTOMER_TYPE_ID =:customerTypeId " +
//                    "AND STATUS = 1 AND PRICE_TYPE = -1 "
//            , nativeQuery = true)
//    Price getProductPrice(Long productId, Long customerTypeId);

    @Query(value = "SELECT p FROM Price p " +
            "WHERE p.status = 1 AND p.productId IN (:productIds) AND p.priceType = -1 AND p.customerTypeId =:customerTypeId " +
            "AND ( :date IS NULL OR (p.fromDate IS NULL AND p.toDate IS NULL) OR ( :date BETWEEN p.fromDate AND p.toDate ) " +
            "OR ( p.fromDate <= :date AND p.toDate IS NULL ) OR ( p.fromDate IS NULL AND :date <= p.toDate ) )")
    List<Price> findProductPrice(List<Long> productIds, Long customerTypeId, LocalDateTime date);

    @Query(value = "SELECT * FROM PRICES WHERE PRODUCT_ID =:productId AND PRICE_TYPE = -1 AND STATUS = 1 " +
            "ORDER BY CUSTOMER_TYPE_ID ASC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    Optional<Price> getByASCCustomerType(Long productId);

    @Query(value = "SELECT * FROM PRICES WHERE PRODUCT_ID =:productId " +
                    "AND STATUS = 1 AND PRICE_TYPE = 1 "
            , nativeQuery = true)
    Price getProductPriceByProductId(Long productId);
}
