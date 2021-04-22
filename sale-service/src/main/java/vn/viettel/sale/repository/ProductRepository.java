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
    @Query(value = "SELECT PRODUCT_CODE FROM PRODUCTS ", nativeQuery = true)
    List<String> getProductCode();

    @Query(value = "SELECT p.ID FROM PRODUCTS p " +
            "LEFT JOIN SALE_ORDER_DETAIL s ON p.ID = s.PRODUCT_ID " +
            "LEFT JOIN SALE_ORDERS so ON so.ID = s.SALE_ORDER_ID  " +
            "WHERE (s.SHOP_ID = :shopId OR s.SHOP_ID IS NULL OR so.TYPE = 1 OR so.TYPE IS NUll) " +
            "AND p.PRODUCT_CODE LIKE %:keyWork% " +
            "OR p.PRODUCT_NAME LIKE %:keyWork% OR p.PRODUCT_NAME_TEXT LIKE %:nameLowerCase% " +
            "GROUP BY p.ID " +
            "ORDER BY nvl(SUM(s.QUANTITY), 0) DESC ", nativeQuery = true
    )
    Page<BigDecimal> findProductTopSale(Long shopId, String keyWork, String nameLowerCase, Pageable pageable);

    Product findProductById(Long productId);
}
