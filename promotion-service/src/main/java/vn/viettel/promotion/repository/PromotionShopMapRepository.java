package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionShopMap;

import java.time.LocalDateTime;

public interface PromotionShopMapRepository extends BaseRepository<PromotionShopMap> {

//    @Query(value = "SELECT p FROM PromotionShopMap p " +
//            "WHERE p.promotionProgramId =:programId AND p.shopId =:shopId AND p.fromDate <=:lastDay AND (p.toDate IS NULL OR p.toDate >=:firstDay) AND p.status = 1 ")
    @Query(value = "SELECT p FROM PromotionShopMap p " +
            "WHERE p.promotionProgramId =:programId AND p.shopId =:shopId AND p.status = 1 ")
    PromotionShopMap findByPromotionProgramIdAndShopId(Long programId, Long shopId);
}
