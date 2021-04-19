package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramDiscount;

import java.util.List;


public interface PromotionRepository extends BaseRepository<PromotionProgramDiscount> {
    @Query(value = "SELECT * FROM PROMOTION_PROGRAM_DISCOUNT WHERE ORDER_NUMBER = :OD", nativeQuery = true)
    List<PromotionProgramDiscount> getPromotionProgramDiscountByOrderNumber(String OD);
}
