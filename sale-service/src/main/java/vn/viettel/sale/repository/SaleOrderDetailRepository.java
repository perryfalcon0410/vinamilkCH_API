package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.SaleOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface SaleOrderDetailRepository extends BaseRepository<SaleOrderDetail> {
    @Query(value = "SELECT * FROM sale_order_deltail", nativeQuery = true)
    List<SaleOrderDetail> getListSaleOrderDetail();
}
