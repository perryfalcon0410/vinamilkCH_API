package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.sale.entities.Product;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {

    Optional<Product> getProductByProductCodeAndStatus(String productCode, Integer status);
    @Query(value = "SELECT ID FROM PRODUCTS ", nativeQuery = true)
    List<BigDecimal> getProductId();

    @Query(value =
            "SELECT p.ID FROM PRODUCTS p " +
            "    JOIN SALE_ORDER_DETAIL ods ON p.ID = ods.PRODUCT_ID " +
            "    JOIN SALE_ORDERS od ON od.id = ods.SALE_ORDER_ID " +
            "WHERE od.SHOP_ID =:shopId AND od.customer_id =:customerId AND ods.IS_FREE_ITEM = 0 AND p.STATUS = 1 " +
            "GROUP BY p.ID " +
            "ORDER BY nvl(SUM(ods.QUANTITY), 0) DESC "
            , nativeQuery = true)
    Page<BigDecimal> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable);

    @Query(value =
            "SELECT p.ID FROM PRODUCTS p " +
            "    JOIN SALE_ORDER_DETAIL ods ON p.ID = ods.PRODUCT_ID " +
            "    JOIN SALE_ORDERS od ON od.id = ods.SALE_ORDER_ID " +
            "WHERE od.SHOP_ID =:shopId " +
            " AND (p.PRODUCT_NAME LIKE %:keyWord% OR p.PRODUCT_NAME_TEXT LIKE %:keyUpper% OR UPPER(p.PRODUCT_CODE) LIKE %:keyUpper% ) " +
            " AND od.ORDER_DATE BETWEEN :fromDate AND :toDate AND ods.IS_FREE_ITEM = 0 AND p.STATUS = 1 " +
            "GROUP BY p.ID " +
            "ORDER BY nvl(SUM(ods.QUANTITY), 0) DESC "
            , nativeQuery = true)
    Page<BigDecimal> findProductsTopSale(Long shopId, String keyWord, String keyUpper, Date fromDate, Date toDate, Pageable pageable);


//    @Query(value = "SELECT * FROM PRODUCTS WHERE STATUS = 1 AND ID = :productId", nativeQuery = true)
    Product findByIdAndStatus(Long productId, int status);
}
