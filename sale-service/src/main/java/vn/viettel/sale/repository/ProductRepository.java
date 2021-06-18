package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.service.dto.FreeProductDTO;
import vn.viettel.sale.service.dto.ProductDetailDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {
    Product findByProductCode(String productCode);

    Optional<Product> getProductByProductCodeAndStatus(String productCode, Integer status);
    @Query(value = "SELECT ID FROM PRODUCTS ", nativeQuery = true)
    List<BigDecimal> getProductId();

    @Query(value =
            "SELECT p.ID FROM PRODUCTS p " +
                    "    JOIN SALE_ORDER_DETAIL ods ON p.ID = ods.PRODUCT_ID " +
                    "    JOIN SALE_ORDERS od ON od.id = ods.SALE_ORDER_ID " +
                    "WHERE od.SHOP_ID =:shopId AND od.customer_id =:customerId AND od.TYPE = 1 AND ods.IS_FREE_ITEM = 0 AND p.STATUS = 1 " +
                    "GROUP BY p.ID " +
                    "ORDER BY nvl(SUM(ods.QUANTITY), 0) DESC "
            , nativeQuery = true)
    Page<BigDecimal> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable);

    @Query(value =
            "SELECT p.ID FROM PRODUCTS p " +
                    "   JOIN SALE_ORDER_DETAIL ods ON p.ID = ods.PRODUCT_ID " +
                    "   JOIN SALE_ORDERS od ON od.id = ods.SALE_ORDER_ID" +
                    "   JOIN STOCK_TOTAL st ON st.PRODUCT_ID = p.ID " +
                    "WHERE od.SHOP_ID =:shopId " +
                    "   AND od.TYPE = 1 AND ods.IS_FREE_ITEM = 0 AND p.STATUS = 1 " +
                    "   AND st.SHOP_ID =:shopId AND st.WARE_HOUSE_TYPE_ID =:warehouse AND st.QUANTITY > 0 AND st.STATUS = 1 " +
                    "   AND ( p.PRODUCT_NAME_TEXT LIKE %:keyUpper% OR UPPER(p.PRODUCT_CODE) LIKE %:keyUpper% ) " +
                    "   AND od.ORDER_DATE BETWEEN :fromDate AND :toDate " +
                    "GROUP BY p.ID " +
                    "ORDER BY nvl(SUM(ods.QUANTITY), 0) DESC "
            , nativeQuery = true)
    Page<BigDecimal> topSaleAndCheckStockTotal(Long shopId, Long warehouse, String keyUpper, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value =
            "SELECT p.ID FROM PRODUCTS p " +
                    "   JOIN SALE_ORDER_DETAIL ods ON p.ID = ods.PRODUCT_ID " +
                    "   JOIN SALE_ORDERS od ON od.id = ods.SALE_ORDER_ID " +
                    "WHERE od.SHOP_ID =:shopId " +
                    "   AND od.TYPE = 1 AND ods.IS_FREE_ITEM = 0 AND p.STATUS = 1 " +
                    "   AND ( p.PRODUCT_NAME_TEXT LIKE %:keyUpper% OR UPPER(p.PRODUCT_CODE) LIKE %:keyUpper% ) " +
                    "   AND od.ORDER_DATE BETWEEN :fromDate AND :toDate " +
                    "GROUP BY p.ID " +
                    "ORDER BY nvl(SUM(ods.QUANTITY), 0) DESC "
            , nativeQuery = true)
    Page<BigDecimal> findProductsTopSale(Long shopId, String keyUpper, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    //    @Query(value = "SELECT * FROM PRODUCTS WHERE STATUS = 1 AND ID = :productId", nativeQuery = true)
    Product findByIdAndStatus(Long productId, int status);

    /*
    lấy thông tin sản phẩm và tồn kho
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.FreeProductDTO ( st.productId, p.productName, p.productCode, st.quantity ) " +
                    "FROM Product p " +
                    "   JOIN StockTotal st ON st.productId = p.id " +
                    "   AND st.shopId =:shopId AND st.wareHouseTypeId =:warehouseId AND st.quantity > 0 AND st.status = 1 " +
                    "   WHERE (p.productNameText LIKE %:keyWord% OR UPPER(p.productCode) LIKE %:keyWord% ) " +
                    "   AND p.status = 1 ")
    Page<FreeProductDTO> findFreeProductDTONoOrder(Long shopId, Long warehouseId, String keyWord, Pageable pageable);

    /*
    lấy thông tin sản phẩm và tồn kho
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.FreeProductDTO ( p.id, p.productName, p.productCode, st.quantity ) " +
            "FROM Product p " +
            "   JOIN StockTotal st ON st.productId = p.id " +
            "   AND st.shopId =:shopId AND st.wareHouseTypeId =:warehouseId AND st.quantity > 0 AND st.status = 1 " +
            "   WHERE p.id =:productId AND p.status = 1 ")
    FreeProductDTO getFreeProductDTONoOrder(Long shopId, Long warehouseId, Long productId);

    /*
    lấy thông tin sản phẩm và tồn kho
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.FreeProductDTO ( p.id, p.productName, p.productCode, st.quantity ) " +
            "FROM Product p " +
            "   JOIN StockTotal st ON st.productId = p.id " +
            "   AND st.shopId =:shopId AND st.wareHouseTypeId =:warehouseId AND st.quantity > 0 AND st.status = 1 " +
            "   WHERE p.id IN :productIds AND p.status = 1 ")
    List<FreeProductDTO> findFreeProductDTONoOrders(Long shopId, Long warehouseId, List<Long> productIds);

    /*
    lấy thông tin ProductDetailDTO
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.ProductDetailDTO ( so.orderNumber, p.productCode, p.productName, p.uom1, p.uom2, p.groupVat" +
            ", soDtl.quantity, soDtl.price, soDtl.amount ) " +
            "FROM Product p " +
            "   JOIN Price price ON price.productId = p.id AND price.status = 1 AND price.priceType = 1 " +
            "   JOIN SaleOrderDetail soDtl ON soDtl.productId = p.id AND soDtl.isFreeItem = false " +
            "   JOIN SaleOrder so ON soDtl.saleOrderId = so.id " +
            "   WHERE so.orderNumber = :orderNumber AND p.status = 1 ")
    List<ProductDetailDTO> findProductDetailDTO(String orderNumber);
}
