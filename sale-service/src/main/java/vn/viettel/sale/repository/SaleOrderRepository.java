package vn.viettel.sale.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface SaleOrderRepository extends BaseRepository<SaleOrder>, JpaSpecificationExecutor<SaleOrder> {
    @Query(value = "SELECT * FROM SALE_ORDERS WHERE TYPE = 1"   , nativeQuery = true)
    List<SaleOrder> getListSaleOrder();

    @Query(value = "SELECT * FROM (SELECT * FROM SALE_ORDERS WHERE CUSTOMER_ID = :id ORDER BY CREATED_AT DESC) WHERE ROWNUM = 1", nativeQuery = true)
    SaleOrder getSaleOrderByCustomerIdAndDeletedAtIsNull(Long id);
    
    @Query(value = "SELECT * FROM SALE_ORDERS WHERE TYPE = 2", nativeQuery = true)
    List<SaleOrder> getListOrderReturn();

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
}
