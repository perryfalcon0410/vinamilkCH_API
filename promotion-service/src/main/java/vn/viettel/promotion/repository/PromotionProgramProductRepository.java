package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramProduct;

import java.util.List;

public interface PromotionProgramProductRepository extends BaseRepository<PromotionProgramProduct> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_PRODUCT WHERE PROMOTION_PROGRAM_ID IN :ids " +
            "AND TYPE = 2", nativeQuery = true)
    List<PromotionProgramProduct> findRejectedProject(List<Long> ids);
}
