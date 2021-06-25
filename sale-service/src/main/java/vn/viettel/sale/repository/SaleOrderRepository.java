package vn.viettel.sale.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.SaleOrderTotalResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleOrderRepository extends BaseRepository<SaleOrder>, JpaSpecificationExecutor<SaleOrder> {

    @Query(value = "SELECT count(so) FROM SaleOrder so WHERE so.fromSaleOrderId = :id AND so.type = 2 ")
    Integer checkIsReturn(Long id);

    @Query(value = "SELECT * FROM SALE_ORDERS WHERE ORDER_NUMBER = :ON", nativeQuery = true)
    SaleOrder getSaleOrderByNumber(String ON);

    @Query(value = "SELECT COUNT(ID)" +
            " FROM SALE_ORDERS" +
            " WHERE trunc(SYSDATE) <= CREATED_AT" +
            " AND CREATED_AT < trunc(SYSDATE)+1", nativeQuery = true)
    Integer countSaleOrder();

    @Query(value = "SELECT customer_id FROM sale_orders WHERE order_number = ?1", nativeQuery = true)
    Long getCustomerCode(String ids);

    SaleOrder findSaleOrderByCustomerIdAndOrderNumberAndType(Long idCus, String saleOrderCode,Integer type);

    Optional<SaleOrder> getSaleOrderByOrderNumber(String saleOrderCode);

    @Query(value = "SELECT id FROM sale_orders WHERE order_number = ?1", nativeQuery = true)
    Long findSaleOrderIdByOrderCode(String orderCode);

    @Query(value = "SELECT order_number FROM sale_orders WHERE id = ?1",nativeQuery = true)
    String findByIdSale(Long saleOrderId);

    @Query(value = "SELECT * FROM SALE_ORDERS WHERE CUSTOMER_ID = :customerId ORDER BY CREATED_AT DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY",nativeQuery = true)
    Optional<SaleOrder> getLastSaleOrderByCustomerId(Long customerId);

    @Query(value = "SELECT COUNT(ID) FROM SALE_ORDERS WHERE TO_CHAR(ORDER_DATE,'DD') = TO_CHAR(SYSDATE,'DD')  ", nativeQuery = true)
    int countIdFromSaleOrder();

    @Query(value = "SELECT SUM(TOTAL) FROM SALE_ORDERS WHERE CUSTOMER_ID =:customerId AND ORDER_DATE >= :fromDate AND ORDER_DATE <= :toDate", nativeQuery = true)
    Double getTotalBillForTheMonthByCustomerId(Long customerId, LocalDate fromDate, LocalDate toDate);

    @Query(value = "SELECT new vn.viettel.sale.messaging.SaleOrderTotalResponse(sum(-so.amount), sum(-so.total), sum(so.totalPromotion) ) " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or so.orderNumber LIKE %:orderNumber% ) AND so.type = 2 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN (:customerIds)) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) "
    )
    SaleOrderTotalResponse getSumSaleOrderReturn(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "SELECT so " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or so.orderNumber LIKE %:orderNumber% ) AND so.type = 2 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN :customerIds ) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) "
    )
    Page<SaleOrder> getSaleOrderReturn(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "SELECT DISTINCT so " +
            " FROM SaleOrder so" +
            " WHERE ( :orderNumber is null or so.orderNumber LIKE %:orderNumber% ) AND so.type = 1 and so.shopId =:shopId " +
            " AND ( COALESCE(:customerIds,NULL) IS NULL OR so.customerId IN :customerIds ) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) " +
            " AND ( so.isReturn is null or so.isReturn = true ) " +
            " AND so.fromSaleOrderId is null " +
            " AND so.id in (select sd.saleOrderId from SaleOrderDetail sd " +
            " JOIN Product p ON p.id = sd.productId and (:keyWord is null or p.productNameText LIKE %:keyWord% OR UPPER(p.productCode) LIKE %:keyWord% ) " +
            "  )"
    )
    Page<SaleOrder> getSaleOrderForReturn(Long shopId, String orderNumber, List<Long> customerIds, String keyWord, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "SELECT so " +
            " FROM SaleOrder so " +
            " WHERE ( :orderNumber is null or so.orderNumber LIKE %:orderNumber% ) AND so.type = 1 and so.shopId =:shopId " +
            " AND (COALESCE(:customerIds, NULL) IS NULL OR so.customerId IN (:customerIds)) " +
            " AND (:fromDate IS NULL OR so.createdAt >= :fromDate) " +
            " AND (:toDate IS NULL OR so.createdAt <= :toDate) " +
            " AND so.fromSaleOrderId is null and (so.usedRedInvoice is null or so.usedRedInvoice = false) "
    )
    Page<SaleOrder> getAllBillOfSaleList(Long shopId, String orderNumber, List<Long> customerIds, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
