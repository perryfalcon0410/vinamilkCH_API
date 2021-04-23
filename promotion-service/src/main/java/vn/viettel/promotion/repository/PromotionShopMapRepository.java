package vn.viettel.promotion.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionShopMap;

public interface PromotionShopMapRepository extends BaseRepository<PromotionShopMap> {
    PromotionShopMap findByPromotionProgramIdAndShopId(Long programId, Long shopId);
}
