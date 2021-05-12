package vn.viettel.sale.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.repository.BaseRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SaleOrderRepository extends BaseRepository<SaleOrder>, JpaSpecificationExecutor<SaleOrder> {
    @Query(value = "SELECT DISTINCT so.* FROM SALE_ORDERS so" +
            " JOIN SALE_ORDER_DETAIL sd ON sd.SALE_ORDER_ID = so.ID" +
            " JOIN PRODUCTS p ON p.ID = sd.PRODUCT_ID" +
            " WHERE so.ORDER_NUMBER LIKE %:orNumber% AND so.TYPE = 1" +
            " AND so.CUSTOMER_ID IN :customerIds" +
            " AND (p.PRODUCT_NAME LIKE %:product% OR p.PRODUCT_NAME_TEXT LIKE %:nameLowerCase% OR p.PRODUCT_CODE LIKE %:product%)" +
            " AND so.CREATED_AT >= :frDate" +
            " AND so.CREATED_AT <= :toDate" +
            " AND so.ID NOT IN :Idr", nativeQuery = true)
    List<SaleOrder> getListSaleOrder(String product, String nameLowerCase, String orNumber, List<Long> customerIds, Date frDate, Date toDate, List<Long> Idr);

    @Query(value = "SELECT FROM_SALE_ORDER_ID FROM SALE_ORDERS WHERE TYPE = 2", nativeQuery = true)
    List<Long> getFromSaleId();

    @Query(value = "SELECT * FROM SALE_ORDERS WHERE ORDER_NUMBER = :ON", nativeQuery = true)
    SaleOrder getSaleOrderByNumber(String ON);

    @Query(value = "SELECT COUNT(ID)" +
                   "FROM SALE_ORDERS WHERE TYPE = 2 " +
            "AND trunc(SYSDATE) <= CREATED_AT " +
            "AND CREATED_AT < trunc(SYSDATE)+1", nativeQuery = true)
    Integer countOrderReturn();

    @Query(value = "SELECT * from SALE_ORDERS where order_number = ?1", nativeQuery = true)
    List<SaleOrder> findByOrderNumber(String id);

    @Query(value = "SELECT customer_id FROM sale_orders WHERE order_number = ?1", nativeQuery = true)
    Long getCustomerCode(String ids);

    SaleOrder findSaleOrderByCustomerIdAndOrderNumber(Long idCus, String saleOrderCode);

    @Query(value = "select order_number from sale_orders where id = ?1", nativeQuery = true)
    String getSaleOrderCode(Long saleOrderId);

    SaleOrder findSaleOrderByOrderNumber(String saleOrderCode);

    @Query(value = "SELECT id FROM sale_orders WHERE order_number = ?1", nativeQuery = true)
    Long findSaleOrderIdByOrderCode(String orderCode);

    @Query(value = "SELECT order_number FROM sale_orders WHERE id = ?1",nativeQuery = true)
    String findByIdSale(Long saleOrderId);

    @Query(value = "SELECT * FROM SALE_ORDERS WHERE CUSTOMER_ID = :customerId ORDER BY CREATED_AT DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY",nativeQuery = true)
    Optional<SaleOrder> getLastSaleOrderByCustomerId(Long customerId);
}
