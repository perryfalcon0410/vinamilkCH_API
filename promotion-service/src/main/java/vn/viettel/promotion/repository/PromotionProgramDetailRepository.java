package vn.viettel.promotion.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramDetail;

import java.util.List;

public interface PromotionProgramDetailRepository extends BaseRepository<PromotionProgramDetail> {

    List<PromotionProgramDetail> findByPromotionProgramIdOrderByFreeQtyDesc(Long id);

}
