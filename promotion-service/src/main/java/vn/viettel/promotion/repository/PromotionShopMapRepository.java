package vn.viettel.promotion.repository;

import vn.viettel.core.db.entity.promotion.PromotionShopMap;
import vn.viettel.core.repository.BaseRepository;

public interface PromotionShopMapRepository extends BaseRepository<PromotionShopMap> {
    PromotionShopMap findByPromotionProgramIdAndShopId(Long programId, Long shopId);
}
