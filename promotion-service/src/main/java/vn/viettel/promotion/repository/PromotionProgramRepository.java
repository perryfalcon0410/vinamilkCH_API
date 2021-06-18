package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgram;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface PromotionProgramRepository extends BaseRepository<PromotionProgram> {
    @Query(value = "SELECT p FROM PromotionProgram p JOIN PromotionShopMap ps ON p.id = ps.promotionProgramId " +
            "WHERE p.status = 1 AND ps.status = 1 AND ps.shopId IN :shopIds " +
            "AND ( :date IS NULL OR (p.fromDate IS NULL AND p.toDate IS NULL) " +
            "OR ( :date BETWEEN p.fromDate AND p.toDate ) " +
            "OR ( p.fromDate <= :date AND p.toDate IS NULL )" +
            "OR ( p.fromDate IS NULL AND :date <= p.toDate ) " +
            ")")
    List<PromotionProgram> findAvailableProgram(List<Long> shopIds, Date date);

    @Query(value = "SELECT * FROM PROMOTION_PROGRAM " +
                    "WHERE PROMOTION_PROGRAM_CODE = :code", nativeQuery = true)
    PromotionProgram findByCode(String code);

    PromotionProgram findByPromotionProgramCode(String code);

    Optional<PromotionProgram> findByIdAndStatus(Long Id, Integer status);
}
