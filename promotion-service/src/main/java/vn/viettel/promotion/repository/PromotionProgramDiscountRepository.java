package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramDiscount;

import java.util.List;
import java.util.Optional;


public interface PromotionProgramDiscountRepository extends BaseRepository<PromotionProgramDiscount> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_DISCOUNT WHERE IS_USED = 1 AND ORDER_NUMBER = :OD", nativeQuery = true)
    List<PromotionProgramDiscount> getPromotionProgramDiscountByOrderNumber(String OD);

    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_DISCOUNT WHERE PROMOTION_PROGRAM_ID IN :ids " +
            "AND CUSTOMER_CODE = :cusCode ", nativeQuery = true)
    List<PromotionProgramDiscount> findPromotionDiscount(List<Long> ids, String cusCode);

    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_DISCOUNT WHERE PROMOTION_PROGRAM_ID = :promotionId " +
            "AND STATUS = 1 AND TYPE IN (0, 3) ", nativeQuery = true)
    List<PromotionProgramDiscount> findPromotionDiscountByPromotion(Long promotionId);

    Optional<PromotionProgramDiscount> findByDiscountCodeAndStatusAndIsUsed(String discountCode, Integer status, Integer isUsed );
}
