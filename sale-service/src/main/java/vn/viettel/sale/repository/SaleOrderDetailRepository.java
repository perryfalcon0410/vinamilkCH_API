package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.sale.SaleOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface SaleOrderDetailRepository extends BaseRepository<SaleOrderDetail> {
    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL", nativeQuery = true)
    List<SaleOrderDetail> getListSaleOrderDetail();

    @Query(value = "SELECT * FROM SALE_ORDER_DETAIL WHERE SALE_ORDER_ID LIKE = :ID", nativeQuery = true)
    List<SaleOrderDetail> getBySaleOrderId(Long ID);
}
