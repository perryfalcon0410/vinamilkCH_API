package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramProduct;

import java.util.List;

public interface PromotionProgramProductRepository extends BaseRepository<PromotionProgramProduct> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_PRODUCT WHERE PROMOTION_PROGRAM_ID IN :ids " +
            "AND TYPE = 2", nativeQuery = true)
    List<PromotionProgramProduct> findRejectedProject(List<Long> ids);

    @Query(value = "SELECT PRODUCT_ID FROM PROMOTION_PROGRAM_PRODUCT" +
            " WHERE PROMOTION_PROGRAM_ID =:prId" +
            " AND PRODUCT_ID IN :productIds" +
            " AND TYPE = 2", nativeQuery = true)
    List<Long> getListProductRejected(Long prId, List<Long> productIds);

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
