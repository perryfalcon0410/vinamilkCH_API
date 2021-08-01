package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramProduct;

import java.util.List;

public interface PromotionProgramProductRepository extends BaseRepository<PromotionProgramProduct> {

    @Query(value = "SELECT pp FROM PromotionProgramProduct pp WHERE pp.promotionProgramId IN :promotionIds And pp.status = 1")
    List<PromotionProgramProduct> findByPromotionIds(List<Long> promotionIds);

    @Query(value = "SELECT pr FROM PromotionProgramProduct pr" +
            " JOIN PromotionProgram p ON p.id = pr.promotionProgramId" +
            " AND pr.type = 1 AND pr.status = 1" +
            " AND p.type = '0' AND p.status = 1 " +
            " AND p.id = :programId")
    List<PromotionProgramProduct> findApplicableProductsNotVAT(Long programId);

    @Query(value = "SELECT pr FROM PromotionProgramProduct pr" +
            " JOIN PromotionProgram p ON p.id = pr.promotionProgramId" +
            " AND pr.type = 1 AND pr.status = 1" +
            " AND p.type = '1' AND p.status = 1 " +
            " AND p.id = :programId")
    List<PromotionProgramProduct> findApplicableProducts(Long programId);
}
