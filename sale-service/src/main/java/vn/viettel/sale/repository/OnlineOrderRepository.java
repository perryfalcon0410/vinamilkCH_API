package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.common.Product;
import vn.viettel.core.db.entity.sale.OnlineOrder;
import vn.viettel.core.repository.BaseRepository;

public interface OnlineOrderRepository extends BaseRepository<OnlineOrder>, JpaSpecificationExecutor<OnlineOrder> {
}
