package vn.viettel.promotion.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProductOpen;

import java.util.List;

public interface PromotionProductOpenRepository extends BaseRepository<PromotionProductOpen> {
    List<PromotionProductOpen> findByPromotionProgramId(Long programId);
}
