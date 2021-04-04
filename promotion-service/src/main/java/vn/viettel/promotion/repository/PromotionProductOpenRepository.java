package vn.viettel.promotion.repository;

import vn.viettel.core.db.entity.promotion.PromotionProductOpen;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PromotionProductOpenRepository extends BaseRepository<PromotionProductOpen> {
    List<PromotionProductOpen> findByPromotionProgramId(Long programId);
}
