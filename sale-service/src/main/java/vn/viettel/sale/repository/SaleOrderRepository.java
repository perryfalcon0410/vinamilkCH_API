package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.SaleOrder;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.saleservice.service.dto.SaleOrderDTO;

import java.util.List;

public interface SaleOrderRepository extends BaseRepository<SaleOrder> {
    @Query(value = "SELECT * FROM sale_order" , nativeQuery = true)
    List<SaleOrderDTO> getListSaleOrder();

    // phai viet :id lien nhau ko la loi
    // xi may cai id ong doi het lai thanh long
}
