package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramDetail;

import java.math.BigDecimal;
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

    @Query(value = "SELECT distinct CASE WHEN EXISTS ( " +
            "SELECT * FROM promotion_program_detail pd join promotion_program p on pd.promotion_program_id = p.id " +
            "WHERE type = :type AND (sale_qty <= :quantity OR sale_amt <= :amount) " +
            "AND product_id IS NOT NULL AND product_id IN :ids AND ( " +
            "(SELECT MAX(product_id) FROM promotion_program_detail where required = 1) IS NOT NULL " +
            "AND product_id in (SELECT distinct product_id FROM promotion_program_detail where required = 1)) " +
            "OR ((SELECT  MAX(product_id) FROM promotion_program_detail) IS NULL " +
            "AND product_id in (SELECT distinct product_id FROM promotion_program_detail))) " +
            "THEN 1 ELSE 0 END FROM promotion_program_detail ", nativeQuery = true)
    Long checkBuyingCondition(String type, Integer quantity, Double amount, List<Long> ids);

    @Query(value = "SELECT distinct PRODUCT_ID FROM promotion_program_detail pd join promotion_program p on pd.promotion_program_id = p.id " +
            "WHERE type = :type AND PRODUCT_ID IS NOT NULL", nativeQuery = true)
    List<BigDecimal> getRequiredProducts(String type);

    List<PromotionProgramDetail> findByPromotionProgramId(Long id);
}
