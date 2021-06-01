package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramDetail;

import java.util.List;

public interface PromotionProgramDetailRepository extends BaseRepository<PromotionProgramDetail> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_DETAIL WHERE PROMOTION_PROGRAM_ID = :id", nativeQuery = true)
    List<PromotionProgramDetail> getPromotionDetailByPromotionId(Long id);

    @Query(value = "SELECT MAX(pd.DISC_PER) FROM PROMOTION_PROGRAM_DETAIL pd JOIN PROMOTION_PROGRAM p " +
            "ON p.ID = pd.PROMOTION_PROGRAM_ID " +
            "WHERE p.TYPE = :type " +
            "AND p.PROMOTION_PROGRAM_CODE = :code " +
            "AND pd.PRODUCT_ID IS NULL " +
            "AND pd.DISC_PER > 0 " +
            "AND :amount >= pd.SALE_AMT", nativeQuery = true)
    Double getDiscountPercent(String type, String code, Double amount);
}
