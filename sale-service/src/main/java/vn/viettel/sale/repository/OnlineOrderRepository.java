package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.OnlineOrder;
import vn.viettel.core.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OnlineOrderRepository extends BaseRepository<OnlineOrder>, JpaSpecificationExecutor<OnlineOrder> {
    OnlineOrder findByOrderNumber(String orderNumber);

    @Query(value = "SELECT onl FROM OnlineOrder onl WHERE onl.synStatus = 1 AND onl.vnmSynStatus = 0 AND onl.shopId =:shopId")
    List<OnlineOrder> findOnlineOrderExportXml(Long shopId);

    @Query(value = "SELECT DISTINCT shopId FROM OnlineOrder WHERE synStatus = 1 AND vnmSynStatus = 0")
    List<Long> findALLShopId();

    @Modifying()
    @Query(value = "Update OnlineOrder SET vnmSynStatus = :vnmSynStatus , vnmSynTime = :vnmSynTime where id =:id")
    int schedulerUpdate(Integer vnmSynStatus, LocalDateTime vnmSynTime, Long id);

    @Modifying()
    @Query(value = "Update OnlineOrder SET synStatus = :synStatus , orderNumber =:orderNumber Where id =:id")
    int schedulerCancel(Integer synStatus, String orderNumber, Long id);

}
