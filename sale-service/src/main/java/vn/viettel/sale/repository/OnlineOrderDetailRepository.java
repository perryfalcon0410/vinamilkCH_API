package vn.viettel.sale.repository;

import vn.viettel.sale.entities.OnlineOrderDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface OnlineOrderDetailRepository extends BaseRepository<OnlineOrderDetail> {
    List<OnlineOrderDetail> findByOnlineOrderId(Long id);
}
