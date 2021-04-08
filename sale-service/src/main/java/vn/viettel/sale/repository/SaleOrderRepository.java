package vn.viettel.sale.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface SaleOrderRepository extends BaseRepository<SaleOrder>, JpaSpecificationExecutor<SaleOrder> {
    @Query(value = "SELECT * FROM SALE_ORDERS WHERE TYPE = 1", nativeQuery = true)
    List<SaleOrder> getListSaleOrder();

    @Query(value = "SELECT * FROM (SELECT * FROM SALE_ORDERS WHERE CUSTOMER_ID = :id ORDER BY CREATED_AT DESC) WHERE ROWNUM = 1", nativeQuery = true)
    SaleOrder getSaleOrderByCustomerIdAndDeletedAtIsNull(Long id);
    
    @Query(value = "SELECT * FROM SALE_ORDERS WHERE TYPE = 2", nativeQuery = true)
    List<SaleOrder> getListOrderReturn();



}
