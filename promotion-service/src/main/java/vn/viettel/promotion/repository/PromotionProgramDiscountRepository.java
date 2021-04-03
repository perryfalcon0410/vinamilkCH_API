package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.promotion.PromotionProgramDiscount;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PromotionProgramDiscountRepository extends BaseRepository<PromotionProgramDiscount> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_DISCOUNT WHERE PROMOTION_PROGRAM_ID IN :ids " +
            "AND CUSTOMER_CODE = :cusCode", nativeQuery = true)
    List<PromotionProgramDiscount> findPromotionDiscount(List<Long> ids, String cusCode);
}
