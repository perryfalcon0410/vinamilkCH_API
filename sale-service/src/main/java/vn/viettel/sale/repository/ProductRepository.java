package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.sale.entities.Product;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {
    Product findByProductCode(String productCode);

    Optional<Product> getProductByProductCodeAndStatus(String productCode, Integer status);
    @Query(value = "SELECT ID FROM PRODUCTS ", nativeQuery = true)
    List<BigDecimal> getProductId();

    @Query(value =
        "SELECT p.ID " +
        "   FROM PRODUCTS p LEFT JOIN" +
            "(" +
            "   SELECT PRODUCT_ID, SUM(QUANTITY) as QUANTITY FROM " +
            "    (SELECT sd.PRODUCT_ID, sd.QUANTITY FROM SALE_ORDER_DETAIL sd JOIN SALE_ORDERS s ON s.ID = sd.SALE_ORDER_ID " +
            "        WHERE s.SHOP_ID =:shopId " +
            "    UNION ALL " +
            "    SELECT  sm.PRODUCT_ID, sm.QUANTITY FROM SALE_ORDER_COMBO_DETAIL sm JOIN SALE_ORDERS s ON s.ID = sm.SALE_ORDER_ID " +
            "        WHERE s.SHOP_ID = :shopId " +
            "    )" +
            "    GROUP BY PRODUCT_ID " +
            ") t ON t.PRODUCT_ID = p.ID " +
        "WHERE ( UPPER(p.PRODUCT_CODE) LIKE %:keyUpper% " +
        "       OR p.PRODUCT_NAME LIKE %:keyWord% OR p.PRODUCT_NAME_TEXT LIKE %:keyUpper% ) " +
        "   AND p.STATUS = 1 " +
        "   ORDER BY NVL(t.QUANTITY, 0) DESC "
        , nativeQuery = true)
    Page<BigDecimal> findProductTopSale(Long shopId, String keyWord, String keyUpper, Pageable pageable);

    @Query(value =
            "SELECT p.ID FROM PRODUCTS p " +
            "    JOIN SALE_ORDER_DETAIL ods ON p.ID = ods.PRODUCT_ID " +
            "    JOIN SALE_ORDERS od ON od.id = ods.SALE_ORDER_ID " +
            "WHERE od.SHOP_ID =:shopId AND od.customer_id =:customerTypeId AND p.STATUS = 1 " +
            "GROUP BY p.ID " +
            "ORDER BY nvl(SUM(ods.QUANTITY), 0) DESC "
            , nativeQuery = true)
    Page<BigDecimal> findProductsCustomerTopSale(Long shopId, Long customerTypeId, Pageable pageable);

//    @Query(value = "SELECT * FROM PRODUCTS WHERE STATUS = 1 AND ID = :productId", nativeQuery = true)
    Product findByIdAndStatus(Long productId, int status);
}
