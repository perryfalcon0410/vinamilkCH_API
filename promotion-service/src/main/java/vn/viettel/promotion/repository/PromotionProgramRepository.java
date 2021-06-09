package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgram;

import java.util.List;
import java.util.Optional;

public interface PromotionProgramRepository extends BaseRepository<PromotionProgram> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM p JOIN PROMOTION_SHOP_MAP ps " +
            "ON p.ID = ps.PROMOTION_PROGRAM_ID WHERE (sysdate BETWEEN p.FROM_DATE AND p.TO_DATE " +
            "OR  sysdate >= p.FROM_DATE )" +
            "AND sysdate BETWEEN ps.FROM_DATE AND ps.TO_DATE " +
            "AND p.STATUS = 1 AND ps.STATUS = 1 " +
            "AND SHOP_ID = :shopId", nativeQuery = true)
    List<PromotionProgram> findAvailableProgram(Long shopId);

    @Query(value = "SELECT * FROM PROMOTION_PROGRAM " +
                    "WHERE PROMOTION_PROGRAM_CODE = :code", nativeQuery = true)
    PromotionProgram findByCode(String code);

    PromotionProgram findByPromotionProgramCode(String code);

    Optional<PromotionProgram> findByIdAndStatus(Long Id, Integer status);


    @Query(value = "SELECT * FROM PROMOTION_PROGRAM p " +
            "JOIN PROMOTION_SHOP_MAP ps ON p.ID = ps.PROMOTION_PROGRAM_ID " +
            "WHERE (sysdate BETWEEN p.FROM_DATE AND p.TO_DATE OR p.TO_DATE IS NULL) " +
            "AND (sysdate BETWEEN ps.FROM_DATE AND ps.TO_DATE OR ps.TO_DATE IS NULL) " +
            "AND p.STATUS = 1 AND ps.STATUS = 1 " +
            "AND ps.SHOP_ID = :shopId", nativeQuery = true)
    List<PromotionProgram> findByShop(Long shopId);
}
