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

    /*
    lấy thông tin sản phẩm có stutus hoạt động
     */
    List<Product> findAllByStatus(Integer i);

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
            "   WHERE p.id IN (:productIds) AND p.status = 1 ")
    List<FreeProductDTO> findFreeProductDTONoOrders(Long shopId, Long warehouseId, List<Long> productIds);

    /*
    lấy thông tin ProductDetailDTO
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.ProductDetailDTO (p.id, so.orderNumber, p.productCode, p.productName, p.uom1, p.uom2, p.groupVat" +
            ", soDtl.quantity, soDtl.price, soDtl.amount ) " +
            "FROM Product p " +
            "JOIN SaleOrderDetail soDtl ON soDtl.productId = p.id AND soDtl.isFreeItem = false " +
            "JOIN SaleOrder so ON soDtl.saleOrderId = so.id " +
            "WHERE so.orderNumber = :orderNumber AND p.status = 1 ")
    List<ProductDetailDTO> findProductDetailDTO(String orderNumber);

    /*
    lấy thông tin OrderProductDTO
     */
    @Query("SELECT NEW vn.viettel.sale.service.dto.OrderProductDTO (p.id, p.productName, p.productCode, price.price, st.quantity, p.status, " +
            "p.uom1, p.isCombo, p.comboProductId, mi.url ) " +
            "FROM Product p " +
            "LEFT JOIN Price price ON price.productId = p.id AND price.status = 1 AND " +
            " (( :customerTypeId IS NULL AND price.priceType = 1) OR (price.customerTypeId = :customerTypeId AND price.priceType = -1)) " +
//            " AND ( :date IS NULL OR (price.fromDate IS NULL AND price.toDate IS NULL) OR ( :date BETWEEN price.fromDate AND price.toDate ) " +
//            " OR ( price.fromDate <= :date AND price.toDate IS NULL ) OR ( price.fromDate IS NULL AND :date <= price.toDate ) )" +
            " AND (:date IS NULL OR 1 = 1) " +
            " JOIN StockTotal st ON st.productId = p.id " +
            " AND st.shopId =:shopId AND st.wareHouseTypeId =:warehouseId AND st.quantity > 0 AND st.status = 1 " +
            " LEFT JOIN MediaItem mi ON mi.objectId = p.id AND mi.status = 1" +
            " WHERE (COALESCE(:productIds, NULL) IS NULL OR p.id IN (:productIds)) " +
            "   AND ( :keyUpper IS NULL OR p.productNameText LIKE %:keyUpper% OR UPPER(p.productCode) LIKE %:keyUpper% ) " +
            " AND (:status IS NULL OR p.status = :status) " +
            " AND (:productInfoId IS NULL OR p.catId = :productInfoId OR p.subCatId = :productInfoId OR p.brandId = :productInfoId OR p.packingId = :productInfoId)")
    Page<OrderProductDTO> findOrderProductDTO(Long shopId, Long customerTypeId, Long warehouseId, List<Long> productIds, String keyUpper,
                                             Integer status, Long productInfoId, LocalDateTime date, Pageable pageable);

    @Query(value =
        "SELECT ods.productId FROM SaleOrderDetail ods JOIN Product p ON ods.productId = p.id AND p.status = 1" +
                "    JOIN SaleOrder od ON od.id = ods.saleOrderId " +
                "WHERE od.shopId = :shopId AND od.customerId =:customerId AND od.type = 1 AND ods.isFreeItem = false " +
                "GROUP BY ods.productId " +
                "ORDER BY coalesce(SUM(ods.quantity), 0) DESC ")
    Page<Long> findProductsCustomerTopSale(Long shopId, Long customerId, Pageable pageable);

    @Query(" SELECT NEW vn.viettel.sale.service.dto.OrderProductDTO (p.id, p.productName, p.productCode, price.price, st.quantity, p.status, " +
            " p.uom1, p.isCombo, p.comboProductId, mi.url ) " +
            " FROM Product p " +
            " LEFT JOIN Price price ON price.productId = p.id AND price.status = 1 AND " +
            " (( :customerTypeId IS NULL AND price.priceType = 1) OR (price.customerTypeId = :customerTypeId AND price.priceType = -1)) " +
//            " AND ( (price.fromDate IS NULL AND price.toDate IS NULL) OR ( :date BETWEEN price.fromDate AND price.toDate ) " +
//            " OR ( price.fromDate <= :date AND price.toDate IS NULL ) OR ( price.fromDate IS NULL AND :date <= price.toDate ) )" +
            " JOIN StockTotal st ON st.productId = p.id AND (:warehouseId IS NULL OR st.wareHouseTypeId = :warehouseId) " +
            "   AND st.shopId =:shopId AND ( :hasQty IS NULL OR :hasQty = false OR ( :hasQty = true AND st.quantity > 0 AND st.status = 1)) " +
            " LEFT JOIN MediaItem mi ON mi.objectId = p.id AND mi.status = 1" +
            " LEFT JOIN SaleOrderDetail ods  ON ods.productId = p.id " +
            " LEFT JOIN SaleOrder od ON od.id = ods.saleOrderId " +
            "   AND od.shopId = :shopId AND(:customerId IS NULL OR od.customerId =:customerId) AND od.type = 1 AND ods.isFreeItem = false " +
            "   AND ( od.orderDate IS NULL OR (od.orderDate BETWEEN :fromDate AND :toDate) )" +
            " WHERE p.status = 1 AND ( :keyUpper IS NULL OR p.productNameText LIKE %:keyUpper% OR UPPER(p.productCode) LIKE %:keyUpper% ) " +
            " GROUP BY p.id, p.productName, p.productCode, price.price, st.quantity, p.status, p.uom1, p.isCombo, p.comboProductId, mi.url " +
            "ORDER BY coalesce(SUM(ods.quantity), 0) DESC ")
    Page<OrderProductDTO> findOrderProductTopSale(Long shopId, Long customerTypeId, Long warehouseId, Long customerId, String keyUpper,
                            LocalDateTime fromDate, LocalDateTime toDate, Boolean hasQty, Pageable pageable);

//    @Query(value =
//            "SELECT p.id FROM Product p " +
//                    " LEFT JOIN SaleOrderDetail ods  ON ods.productId = p.id " +
//                    "   LEFT JOIN SaleOrder od ON od.id = ods.saleOrderId " +
//                    "   AND od.shopId = :shopId AND(:customerId IS NULL OR od.customerId =:customerId) AND od.type = 1 AND ods.isFreeItem = false " +
//                    "   AND ( od.orderDate IS NULL OR (od.orderDate BETWEEN :fromDate AND :toDate) )" +
//                    "   JOIN StockTotal st ON st.productId = p.id AND (:warehouseId IS NULL OR st.wareHouseTypeId = :warehouseId) " +
//                    "   AND st.shopId =:shopId AND ( :hasQty IS NULL OR :hasQty = false OR ( :hasQty = true AND st.quantity > 0 AND st.status = 1)) " +
//                    "WHERE  " +
//                    "    ( p.productNameText LIKE %:keyUpper% OR UPPER(p.productCode) LIKE %:keyUpper% ) " +
//                    " AND p.status = 1 " +
//                    "GROUP BY p.id " +
//                    "ORDER BY coalesce(SUM(ods.quantity), 0) DESC " +
//                    "")
//    Page<Long> findProductsTopSale(String shopId, Long customerId, Long warehouseId, String keyUpper, LocalDateTime fromDate, LocalDateTime toDate, Boolean hasQty, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.id IN (:productIds) AND (:status IS null or p.status = :status )")
    List<Product> getProducts(List<Long> productIds, Integer status);
}
