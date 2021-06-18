package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.Product;
import vn.viettel.sale.service.dto.FreeProductDTO;
import vn.viettel.sale.service.dto.OrderProductDTO;
import vn.viettel.sale.service.dto.ProductDetailDTO;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product>, JpaSpecificationExecutor<Product> {
    Product findByProductCode(String productCode);

    Optional<Product> getProductByProductCodeAndStatus(String productCode, Integer status);
    @Query(value = "SELECT ID FROM PRODUCTS ", nativeQuery = true)
    List<BigDecimal> getProductId();

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
            "JOIN SaleOrderDetail soDtl ON soDtl.productId = p.id AND soDtl.isFreeItem = false " +
            "JOIN SaleOrder so ON soDtl.saleOrderId = so.id " +
            "WHERE so.orderNumber = :orderNumber AND p.status = 1 ")
    List<ProductDetailDTO> findProductDetailDTO(String orderNumber);

    /*
    lấy thông tin OrderProductDTO
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.OrderProductDTO ( p.productName, p.productCode, price.price, st.quantity, p.status, " +
            "p.uom1, p.isCombo, p.comboProductId, mi.url ) " +
            "FROM Product p " +
            "LEFT JOIN Price price ON price.productId = p.id AND price.status = 1 AND " +
            "(( :customerTypeId IS NULL AND price.priceType = 1) OR (price.customerTypeId = :customerTypeId AND price.priceType = -1)) " +
            "AND ( :date IS NULL OR (price.fromDate IS NULL AND price.toDate IS NULL) OR ( :date BETWEEN price.fromDate AND price.toDate ) " +
            "OR ( price.fromDate <= :date AND price.toDate IS NULL ) OR ( price.fromDate IS NULL AND :date <= price.toDate ) )" +
            "JOIN StockTotal st ON st.productId = p.id " +
            "AND st.shopId =:shopId AND st.wareHouseTypeId =:warehouseId AND st.quantity > 0 AND st.status = 1 " +
            "LEFT JOIN MediaItem mi ON mi.objectId = p.id AND mi.status = 1" +
            "WHERE (:productIds IS NULL OR p.id IN :productIds) " +
            "   AND ( :keyUpper IS NULL OR p.productNameText LIKE %:keyUpper% OR UPPER(p.productCode) LIKE %:keyUpper% ) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:productInfoId IS NULL OR p.catId = :productInfoId OR p.subCatId = :productInfoId OR p.brandId = :productInfoId OR p.packingId = :productInfoId)")
    Page<OrderProductDTO> findOrderProductDTO(Long shopId, Long customerTypeId, Long warehouseId, List<Long> productIds, String keyUpper,
                                             Integer status, Long productInfoId, Date date, Pageable pageable);

    @Query(value =
        "SELECT ods.productId FROM SaleOrderDetail ods JOIN Product p ON ods.productId = p.id AND p.status = 1" +
                "    JOIN SaleOrder od ON od.id = ods.saleOrderId " +
                "WHERE od.shopId = :shopId AND od.customerId =:customerId AND od.type = 1 AND ods.isFreeItem = false " +
                "GROUP BY ods.productId " +
                "ORDER BY coalesce(SUM(ods.quantity), 0) DESC ")
    Page<Long> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable);

    @Query(value =
            "SELECT ods.productId FROM SaleOrderDetail ods JOIN Product p ON ods.productId = p.id AND p.status = 1" +
                    "    JOIN SaleOrder od ON od.id = ods.saleOrderId " +
                    "   JOIN StockTotal st ON st.productId = p.id AND (:warehouseId IS NULL OR st.wareHouseTypeId = :warehouseId) " +
                    "   AND st.shopId =:shopId AND ( :hasQty IS NULL OR :hasQty = false OR ( :hasQty = true AND st.quantity > 0 AND st.status = 1)) " +
                    "WHERE od.shopId = :shopId AND od.customerId =:customerId AND od.type = 1 AND ods.isFreeItem = false " +
                    "   AND ( p.productNameText LIKE %:keyUpper% OR UPPER(p.productCode) LIKE %:keyUpper% ) " +
                    "   AND od.orderDate BETWEEN :fromDate AND :toDate " +
                    "GROUP BY ods.productId " +
                    "ORDER BY coalesce(SUM(ods.quantity), 0) DESC ")
    Page<Long> findProductsTopSale(Long shopId, Long warehouseId, String keyUpper, LocalDateTime fromDate, LocalDateTime toDate, Boolean hasQty, Pageable pageable);
}
