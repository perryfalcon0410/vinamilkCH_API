package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface SaleOrderRepository extends BaseRepository<SaleOrder> {
    @Query(value = "SELECT * FROM sale_order", nativeQuery = true)
    List<SaleOrder> getListSaleOrder();

    // phai viet :id lien nhau ko la loi
    // xi may cai id ong doi het lai thanh long
    @Query(value = "SELECT * FROM sale_order " +
            "WHERE SALE_ORDER_ID = :id", nativeQuery = true)
    SaleOrder getSaleOrder(int id);
}
