package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface OnlineOrderRepository extends BaseRepository<OnlineOrder>, JpaSpecificationExecutor<OnlineOrder> {
    OnlineOrder findByOrderNumber(String orderNumber);
}
