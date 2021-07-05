package vn.viettel.sale.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;
import vn.viettel.sale.messaging.TotalRedInvoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SaleOrderRepository extends BaseRepository<SaleOrder>, JpaSpecificationExecutor<SaleOrder> {

    @Query(value = "SELECT count(so) FROM SaleOrder so WHERE so.fromSaleOrderId = :id AND so.type = 2 ")
    Integer checkIsReturn(Long id);

    @Query(value = "SELECT so FROM SaleOrder so WHERE so.orderNumber = :ON AND so.type = 1")
    SaleOrder getSaleOrderByNumber(String ON);

    @Query(value = "SELECT so FROM SaleOrder so WHERE so.orderNumber = :ON AND so.type = 2")
    SaleOrder getOrderReturnByNumber(String ON);

    @Query(value = "SELECT COUNT(id)" +
            " FROM SaleOrder" +
            " WHERE createdAt >= :startDate" +
            " AND createdAt <= :endDate" +
            " AND shopId = :shopId")
    Integer countSaleOrder(LocalDateTime startDate, LocalDateTime endDate, Long shopId);

    @Query(value = "SELECT customerId FROM SaleOrder WHERE coalesce(:orderNumbers, null) is null or orderNumber in (:orderNumbers) ")
    List<Long> getCustomerCode(List<String> orderNumbers);

    SaleOrder findSaleOrderByCustomerIdAndOrderNumberAndType(Long idCus, String saleOrderCode,Integer type);

    Optional<SaleOrder> getSaleOrderByOrderNumber(String saleOrderCode);

    @Query(value = "SELECT so FROM SaleOrder so WHERE coalesce(:orderCodes, null) is null or so.orderNumber in :orderCodes")
    List<SaleOrder> findSaleOrderIdByOrderCode(List<String> orderCodes);

    @Query(value = "SELECT orderNumber FROM SaleOrder WHERE id = ?1")
    String findByIdSale(Long saleOrderId);

    @Query(value = "SELECT so FROM SaleOrder so WHERE so.customerId = :customerId ORDER BY so.createdAt DESC ")
    List<SaleOrder> getLastSaleOrderByCustomerId(Long customerId);

    @Query(value = "SELECT COUNT(ID) FROM SALE_ORDERS WHERE TO_CHAR(ORDER_DATE,'DD') = TO_CHAR(SYSDATE,'DD')  ", nativeQuery = true)
    int countIdFromSaleOrder();

    @Query(value = "SELECT SUM(total) FROM SaleOrder WHERE customerId =:customerId AND orderDate BETWEEN :fromDate AND :toDate ")
    Double getTotalBillForTheMonthByCustomerId(Long customerId, LocalDate fromDate, LocalDate toDate);

    @Query(value = "SELECT new vn.viettel.sale.messaging.SaleOrderTotalResponse(sum(-so.amount), sum(-so.total), sum(so.totalPromotion) ) " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 2 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN (:customerIds)) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) "
    )
    SaleOrderTotalResponse getSumSaleOrderReturn(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "SELECT so " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 2 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN :customerIds ) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) "
    )
    Page<SaleOrder> getSaleOrderReturn(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "SELECT DISTINCT so " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 1 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN :customerIds ) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) " +
            " AND ( so.isReturn is null or so.isReturn = false ) " +
            " AND so.fromSaleOrderId is null " +
            " AND so.id in (select sd.saleOrderId from SaleOrderDetail sd " +
            " JOIN Product p ON p.id = sd.productId and (:keyWord is null or p.productNameText LIKE %:keyWord% OR UPPER(p.productCode) LIKE %:keyWord% ) " +
            "  )"
    )
    Page<SaleOrder> getSaleOrderForReturn(Long shopId, String orderNumber, List<Long> customerIds, String keyWord, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "SELECT so " +
            " FROM SaleOrder so " +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 1 and so.shopId =:shopId " +
            " AND (COALESCE(:customerIds, NULL) IS NULL OR so.customerId IN (:customerIds)) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) " +
            " AND so.fromSaleOrderId is null and (so.usedRedInvoice is null or so.usedRedInvoice = false) "
    )
    Page<SaleOrder> getAllBillOfSaleList(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.messaging.SaleOrderTotalResponse(SUM(sbt.amount), SUM(sbt.total), SUM(sbt.totalPromotion)) " +
            "FROM   SaleOrder sbt " +
            "WHERE  sbt.type = :type " +
            "       AND (coalesce(:customerIds, null) IS NULL OR sbt.customerId in :customerIds ) " +
            "       AND (:usedRedInv IS NULL OR (:usedRedInv = 1 and sbt.usedRedInvoice = true ) OR (:usedRedInv = 0 and (sbt.usedRedInvoice is null or sbt.usedRedInvoice = false) ) ) " +
            "       AND (:orderNumber IS NULL OR upper(sbt.orderNumber) LIKE %:orderNumber% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND ( (:fromDate is null AND :toDate is null) OR (:fromDate is null AND sbt.orderDate <= :toDate ) " +
            "           OR  (:toDate is null AND :fromDate <= sbt.orderDate ) OR (sbt.orderDate BETWEEN :fromDate AND :toDate) ) " +
            "")
    SaleOrderTotalResponse getSaleOrderTotal(Long shopId, List<Long> customerIds, String orderNumber, int type,
                                             Integer usedRedInv, LocalDateTime fromDate, LocalDateTime toDate);
    @Query(value = "" +
            "        SELECT pro.productName " +
            "        FROM SaleOrder sale " +
            "        JOIN SaleOrderDetail detail on sale.id = detail.saleOrderId " +
            "        JOIN Product pro on pro.id = detail.productId " +
            "        WHERE sale.customerId = :customerId AND sale.orderDate BETWEEN :fromDate AND :toDate " +
            "        GROUP BY pro.productName " +
            "        ORDER BY coalesce(SUM(detail.quantity), 0) DESC, pro.productName ")
    Page<String> getTopFiveFavoriteProducts(Long customerId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
