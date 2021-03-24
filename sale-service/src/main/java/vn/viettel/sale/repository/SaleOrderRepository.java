package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.sale.SaleOrder;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.SaleOrderDTO;

import java.util.List;

public interface SaleOrderRepository extends BaseRepository<SaleOrder> {
    @Query(value = "SELECT * FROM sale_order" , nativeQuery = true)
    List<SaleOrderDTO> getListSaleOrder();
}
