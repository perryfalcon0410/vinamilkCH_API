package vn.viettel.sale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.OnlineOrderDetail;

public interface OnlineOrderDetailRepository extends BaseRepository<OnlineOrderDetail> {

    List<OnlineOrderDetail> findByOnlineOrderId(Long id);

    @Modifying
    @Query("Delete from OnlineOrderDetail p where p.onlineOrderId=:id")
    void deleteByOnlineOrderId(Long id);

}
