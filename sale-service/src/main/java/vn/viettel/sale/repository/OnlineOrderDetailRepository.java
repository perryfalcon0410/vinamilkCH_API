package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.sale.entities.OnlineOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OnlineOrderDetailRepository extends BaseRepository<OnlineOrderDetail> {

    List<OnlineOrderDetail> findByOnlineOrderId(Long id);

    @Modifying
    @Query("Delete from OnlineOrderDetail p where p.onlineOrderId=:id")
    void deleteByOnlineOrderId(Long id);

}
