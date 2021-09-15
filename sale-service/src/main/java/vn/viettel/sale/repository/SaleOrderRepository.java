package vn.viettel.sale.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleOrderRepository extends BaseRepository<SaleOrder>, JpaSpecificationExecutor<SaleOrder> {

    @Query(value = "SELECT s FROM SaleOrder s " +
            "  WHERE s.shopId =:shopId " +
            "   AND ( COALESCE(:customerIds,NULL) IS NULL OR s.customerId IN (:customerIds)) " +
            "   AND ( :fromDate IS NULL OR s.orderDate >= :fromDate) " +
            "   AND ( :toDate IS NULL OR s.orderDate <= :toDate) " +
            "   AND ( :orderNumber IS NULL OR upper(s.orderNumber) LIKE %:orderNumber% ) " +
            "   AND ( :type IS NULL OR s.type =:type) " +
            "   AND ( :usedRedInvoice IS NULL OR s.usedRedInvoice =:usedRedInvoice ) "
            )
    Page<SaleOrder> findALlSales(List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate,
                                           String orderNumber, Integer type, Long shopId, Boolean usedRedInvoice, Pageable pageable);


    @Query(value = "SELECT distinct s.customerId FROM SaleOrder s " +
            "  WHERE s.shopId =:shopId " +
            "   AND ( :fromDate IS NULL OR s.orderDate >= :fromDate) " +
            "   AND ( :toDate IS NULL OR s.orderDate <= :toDate) " +
            "   AND ( :orderNumber IS NULL OR upper(s.orderNumber) LIKE %:orderNumber% ) " +
            "   AND ( :type IS NULL OR s.type =:type) " +
            "   AND ( :usedRedInvoice IS NULL OR s.usedRedInvoice =:usedRedInvoice ) ")
    List<Long> getCustomerIds( LocalDateTime fromDate, LocalDateTime toDate, String orderNumber, Integer type, Long shopId, Boolean usedRedInvoice);


    @Query(value = "SELECT s FROM SaleOrder s " +
            "  WHERE s.shopId =:shopId " +
            "   AND ( COALESCE(:customerIds,NULL) IS NULL OR s.customerId IN (:customerIds)) " +
            "   AND ( :fromDate IS NULL OR s.orderDate >= :fromDate) " +
            "   AND ( :toDate IS NULL OR s.orderDate <= :toDate) " +
            "   AND ( :orderNumber IS NULL OR upper(s.orderNumber) LIKE %:orderNumber% ) " +
            "   AND ( :type IS NULL OR s.type =:type) " +
            "   AND ( :usedRedInvoice IS NULL OR s.usedRedInvoice =:usedRedInvoice ) " +
            " ORDER BY decode(s.customerId, :customerIdsSort)")
    Page<SaleOrder> findALlSales(List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate,
                                 String orderNumber, Integer type, Long shopId, Boolean usedRedInvoice, List<Long> customerIdsSort, Pageable pageable);

    @Query(value = "SELECT count(so) FROM SaleOrder so WHERE so.fromSaleOrderId = :id AND so.type = 2 ")
    Integer checkIsReturn(Long id);

    @Query(value = "SELECT so FROM SaleOrder so WHERE so.orderNumber = :ON AND so.shopId =:shopId AND so.type = 1")
    SaleOrder getSaleOrderByNumber(String ON, Long shopId);

    @Query(value = "SELECT so FROM SaleOrder so WHERE so.id In (:ids) AND so.shopId =:shopId AND so.type = 1")
    List<SaleOrder> findAllById(List<Long> ids, Long shopId);

   /* @Lock(LockModeType.PESSIMISTIC_WRITE )
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "1000")})*/
    @Query(value = "SELECT s FROM SaleOrder s WHERE s.shopId =:shopId And s.createdAt>= :startDate" +
            " AND s.orderNumber like :startWith% AND s.id is not null" +
            " ORDER BY s.orderNumber desc ")
    Page<SaleOrder> getLastSaleOrderNumber(Long shopId, String startWith, LocalDateTime startDate, Pageable pageable);

    @Query(value = "SELECT customerId FROM SaleOrder WHERE coalesce(:orderNumbers, null) is null or orderNumber in (:orderNumbers) ")
    List<Long> getCustomerCode(List<String> orderNumbers);

    SaleOrder findSaleOrderByCustomerIdAndOrderNumberAndType(Long idCus, String saleOrderCode,Integer type);

    @Query(value = "SELECT so FROM SaleOrder so WHERE coalesce(:orderCodes, null) is null or so.orderNumber in :orderCodes")
    List<SaleOrder> findSaleOrderByOrderCode(List<String> orderCodes);

    @Query(value = "SELECT so FROM SaleOrder so WHERE so.customerId = :customerId ORDER BY so.orderDate DESC ")
    List<SaleOrder> getLastSaleOrderByCustomerId(Long customerId);

    @Query(value = "SELECT SUM(total) FROM SaleOrder WHERE customerId =:customerId AND orderDate BETWEEN :fromDate AND :toDate ")
    Double getTotalBillForTheMonthByCustomerId(Long customerId, LocalDate fromDate, LocalDate toDate);

    @Query(value = "SELECT new vn.viettel.sale.messaging.SaleOrderTotalResponse(sum(-so.amount), sum(-so.total), sum(-so.totalPromotionVat) ) " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 2 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN (:customerIds)) " +
            " AND (:fromDate IS NULL OR so.orderDate >= :fromDate) " +
            " AND (:toDate IS NULL OR so.orderDate <= :toDate) "
    )
    SaleOrderTotalResponse getSumSaleOrderReturn(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "SELECT so " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 2 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN :customerIds ) " +
            " AND (:fromDate IS NULL OR so.orderDate >= :fromDate) " +
            " AND (:toDate IS NULL OR so.orderDate <= :toDate) "
    )
    Page<SaleOrder> getSaleOrderReturn(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "SELECT DISTINCT so " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 1 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN :customerIds ) " +
            " AND (:fromDate IS NULL OR so.orderDate >= :fromDate) " +
            " AND (:toDate IS NULL OR so.orderDate <= :toDate) " +
            " AND ( so.orderDate >= :newFromDate) " +
            " AND ( so.isReturn is null or so.isReturn = true ) " +
            " AND so.fromSaleOrderId is null " +
            " AND so.id in (select sd.saleOrderId from SaleOrderDetail sd " +
            "               JOIN Product p ON p.id = sd.productId and (:keyWord is null or p.productNameText LIKE %:keyWord% OR UPPER(p.productCode) LIKE %:keyWord% ) " +
            "               ) " +
            " ORDER BY so.orderDate desc , so.orderNumber"
    )
    List<SaleOrder> getSaleOrderForReturn(Long shopId, String orderNumber, List<Long> customerIds, String keyWord, LocalDateTime fromDate, LocalDateTime toDate,LocalDateTime newFromDate);

    /*Đơn có SP bán */
    @Query(value = "SELECT distinct so " +
            " FROM SaleOrder so join SaleOrderDetail de On de.saleOrderId = so.id" +
            " WHERE ( :orderNumber is null or upper(so.orderNumber) LIKE %:orderNumber% ) AND so.type = 1 and so.shopId =:shopId " +
            " AND (COALESCE(:customerIds, NULL) IS NULL OR so.customerId IN (:customerIds)) " +
            " AND (:fromDate IS NULL OR so.orderDate >= :fromDate) " +
            " AND (:toDate IS NULL OR so.orderDate <= :toDate) " +
            " AND so.fromSaleOrderId is null and (so.usedRedInvoice is null or so.usedRedInvoice = false) " +
            " AND de.isFreeItem = 0 "
    )
    Page<SaleOrder> getAllBillOfSaleList(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.messaging.SaleOrderTotalResponse(SUM(sbt.amount), SUM(sbt.total), SUM(sbt.totalPromotionVat)) " +
            "FROM   SaleOrder sbt " +
            "WHERE  sbt.type = :type " +
            "       AND (coalesce(:customerIds, null) IS NULL OR sbt.customerId in :customerIds ) " +
            "       AND ( :usedRedInv IS NULL OR sbt.usedRedInvoice =:usedRedInv )" +
/*            "       AND (:usedRedInv IS NULL OR (:usedRedInv = 1 and sbt.usedRedInvoice = true ) OR (:usedRedInv = 0 and (sbt.usedRedInvoice is null or sbt.usedRedInvoice = false) ) ) " +*/
            "       AND (:orderNumber IS NULL OR upper(sbt.orderNumber) LIKE %:orderNumber% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            /*"       AND ( (:fromDate is null AND :toDate is null) OR (:fromDate is null AND sbt.orderDate <= :toDate ) " +
            "           OR  (:toDate is null AND :fromDate <= sbt.orderDate ) OR (sbt.orderDate BETWEEN :fromDate AND :toDate) ) " +*/
            "       AND (:fromDate is null  OR  sbt.orderDate >= :fromDate ) " +
            "       AND (:toDate is null  OR sbt.orderDate <= :toDate ) " +
            "")
    SaleOrderTotalResponse getSaleOrderTotal(Long shopId, List<Long> customerIds, String orderNumber, int type,
                                             Boolean usedRedInv, LocalDateTime fromDate, LocalDateTime toDate);
    @Query(value = "" +
            "        SELECT pro.productName " +
            "        FROM SaleOrder sale " +
            "        JOIN SaleOrderDetail detail on sale.id = detail.saleOrderId " +
            "        JOIN Product pro on pro.id = detail.productId " +
            "        WHERE sale.customerId = :customerId AND sale.orderDate BETWEEN :fromDate AND :toDate " +
            "        GROUP BY pro.productName " +
            "        ORDER BY coalesce(SUM(detail.quantity), 0) DESC, pro.productName ")
    Page<String> getTopFiveFavoriteProducts(Long customerId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);


    @Query(value = "Select so From SaleOrder so Where so.onlineNumber = :onlineNumber And (:startDate Is null Or so.orderDate >= :startDate)")
    List<SaleOrder> checkOnlineNumber(String onlineNumber, LocalDateTime startDate);
}
