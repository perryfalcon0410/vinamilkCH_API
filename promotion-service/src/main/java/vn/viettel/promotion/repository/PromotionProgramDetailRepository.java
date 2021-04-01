package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.promotion.PromotionProgramDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PromotionProgramDetailRepository extends BaseRepository<PromotionProgramDetail> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_DETAIL WHERE PROMOTION_PROGRAM_ID = :id", nativeQuery = true)
    List<PromotionProgramDetail> getPromotionDetailByPromotionId(Long id);
}
