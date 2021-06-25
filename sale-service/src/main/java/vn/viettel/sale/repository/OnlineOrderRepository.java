package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface OnlineOrderRepository extends BaseRepository<OnlineOrder>, JpaSpecificationExecutor<OnlineOrder> {
    OnlineOrder findByOrderNumber(String orderNumber);

    @Query(value = "SELECT * FROM ONLINE_ORDER WHERE SYN_STATUS = 1 AND VNM_SYN_STATUS = 0 AND SHOP_ID =:shopId", nativeQuery = true)
    List<OnlineOrder> findOnlineOrderExportXml(Long shopId);
}
