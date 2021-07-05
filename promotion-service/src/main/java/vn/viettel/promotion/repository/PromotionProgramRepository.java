package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgram;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionProgramRepository extends BaseRepository<PromotionProgram> {
    @Query(value = "SELECT DISTINCT p FROM PromotionProgram p JOIN PromotionShopMap ps ON p.id = ps.promotionProgramId " +
            " WHERE p.status = 1 AND p.fromDate <=:lastDay AND ( p.toDate IS NULL OR p.toDate >=:firstDay ) " +
            " AND ps.status = 1 AND ps.shopId IN :shopIds " +
            " AND ps.fromDate <=:lastDay AND ( ps.toDate IS NULL OR ps.toDate >=:firstDay )")
    List<PromotionProgram> findAvailableProgram(List<Long> shopIds, LocalDateTime firstDay, LocalDateTime lastDay);

    @Query(value = "SELECT * FROM PROMOTION_PROGRAM " +
                    "WHERE PROMOTION_PROGRAM_CODE = :code", nativeQuery = true)
    PromotionProgram findByCode(String code);

    PromotionProgram findByPromotionProgramCode(String code);

    Optional<PromotionProgram> findByIdAndStatus(Long Id, Integer status);
}
