package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramProduct;

import java.util.List;

public interface PromotionProgramProductRepository extends BaseRepository<PromotionProgramProduct> {

    @Query(value = "SELECT pp FROM PromotionProgramProduct pp WHERE pp.promotionProgramId IN :promotionIds "
            )
    List<PromotionProgramProduct> findByPromotionIds(List<Long> promotionIds);

    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_PRODUCT pr" +
            " JOIN PROMOTION_PROGRAM p ON p.ID = pr.PROMOTION_PROGRAM_ID" +
            " AND pr.TYPE = 1" +
            " AND p.TYPE = 0" +
            " AND p.ID = id", nativeQuery = true)
    List<PromotionProgramProduct> findApplicableProductsNotVAT(Long id);

    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_PRODUCT pr" +
            " JOIN PROMOTION_PROGRAM p ON p.ID = pr.PROMOTION_PROGRAM_ID" +
            " AND pr.TYPE = 1" +
            " AND p.TYPE = 1" +
            " AND p.ID = id", nativeQuery = true)
    List<PromotionProgramProduct> findApplicableProducts(Long id);
}
