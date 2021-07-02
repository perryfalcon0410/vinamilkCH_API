package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

public interface OnlineOrderRepository extends BaseRepository<OnlineOrder>, JpaSpecificationExecutor<OnlineOrder> {
    OnlineOrder findByOrderNumber(String orderNumber);

    @Query(value = "SELECT onl FROM OnlineOrder onl WHERE onl.synStatus = 1 AND onl.vnmSynStatus = 0 AND onl.shopId =:shopId")
    List<OnlineOrder> findOnlineOrderExportXml(Long shopId);

    @Query(value = "SELECT DISTINCT shopId FROM OnlineOrder WHERE synStatus = 1 AND vnmSynStatus = 0")
    List<BigDecimal> findALLShopId();
}
