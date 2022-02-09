package vn.viettel.sale.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.OnlineOrder;

public interface OnlineOrderRepository extends BaseRepository<OnlineOrder>, JpaSpecificationExecutor<OnlineOrder> {
    OnlineOrder findByOrderNumber(String orderNumber);

    @Query(value = "SELECT onl FROM OnlineOrder onl WHERE onl.id =:id AND onl.shopId =:shopId AND onl.synStatus = 0")
    Optional<OnlineOrder> getById(Long id, Long shopId);

    @Query(value = "SELECT onl FROM OnlineOrder onl WHERE onl.synStatus = 1 AND onl.vnmSynStatus = 0 AND onl.shopId =:shopId")
    List<OnlineOrder> findOnlineOrderExportXml(Long shopId);

    @Query(value = "SELECT DISTINCT shopId FROM OnlineOrder WHERE synStatus = 1 AND vnmSynStatus = 0")
    List<Long> findALLShopId();

    @Modifying()
    @Query(value = "Update OnlineOrder SET vnmSynStatus = :vnmSynStatus , vnmSynTime = :vnmSynTime, updatedAt = :vnmSynTime, updatedBy= 'schedule' where id =:id")
    int schedulerUpdate(Integer vnmSynStatus, LocalDateTime vnmSynTime, Long id);

    @Modifying()
    @Query(value = "Update OnlineOrder SET synStatus = :synStatus , orderNumber =:orderNumber, updatedAt = :vnmSynTime, updatedBy= 'schedule' Where id =:id")
    int schedulerCancel(Integer synStatus, String orderNumber, LocalDateTime vnmSynTime, Long id);

}
