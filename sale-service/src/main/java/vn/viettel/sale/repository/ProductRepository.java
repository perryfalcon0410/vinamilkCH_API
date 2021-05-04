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
    Product getProductByProductCode(String productCode);

    Optional<Product> getProductByProductCodeAndStatus(String productCode, Integer status);
    @Query(value = "SELECT ID FROM PRODUCTS ", nativeQuery = true)
    List<BigDecimal> getProductId();

    @Query(value =
        "WITH TEMPTABLE " +
        "AS ( " +
            "   SELECT PRODUCT_ID, SUM(QUANTITY) as QUANTITY FROM " +
            "    (SELECT sd.PRODUCT_ID, sd.QUANTITY FROM SALE_ORDER_DETAIL sd JOIN SALE_ORDERS s ON s.ID = sd.SALE_ORDER_ID " +
            "        WHERE s.SHOP_ID =:shopId " +
            "    UNION ALL " +
            "    SELECT  sm.PRODUCT_ID, sm.QUANTITY FROM SALE_ORDER_COMBO_DETAIL sm JOIN SALE_ORDERS s ON s.ID = sm.SALE_ORDER_ID " +
            "        WHERE s.SHOP_ID = :shopId " +
            "    )" +
            "     TEMPTABLE GROUP BY PRODUCT_ID " +
            ") " +
        "SELECT p.ID FROM PRODUCTS p LEFT JOIN TEMPTABLE t ON t.PRODUCT_ID = p.ID " +
        "   WHERE p.PRODUCT_CODE LIKE %:keyWork% " +
        "       OR p.PRODUCT_NAME LIKE %:keyWork% OR p.PRODUCT_NAME_TEXT LIKE %:nameLowerCase% " +
        "   ORDER BY NVL(t.QUANTITY, 0) DESC "
        , nativeQuery = true)
    Page<BigDecimal> findProductTopSale(Long shopId, String keyWork, String nameLowerCase, Pageable pageable);

    @Query(value =
            "SELECT p.ID FROM PRODUCTS p " +
            "    JOIN SALE_ORDER_DETAIL ods ON p.ID = ods.PRODUCT_ID " +
            "    JOIN SALE_ORDERS od ON od.id = ods.SALE_ORDER_ID " +
            "WHERE od.SHOP_ID =:shopId AND od.customer_id =:customerTypeId " +
            "GROUP BY p.ID " +
            "ORDER BY nvl(SUM(ods.QUANTITY), 0) DESC "
            , nativeQuery = true)
    Page<BigDecimal> findProductsCustomerTopSale(Long shopId, Long customerTypeId, Pageable pageable);


    Product findProductById(Long productId);
}
