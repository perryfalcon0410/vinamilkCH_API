package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramDiscount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionProgramDiscountRepository extends BaseRepository<PromotionProgramDiscount> {

    @Query(value = "SELECT p FROM PromotionProgramDiscount p WHERE p.isUsed = 1 AND p.orderNumber = :OD")
    List<PromotionProgramDiscount> getPromotionProgramDiscountByOrderNumber(String OD);

    @Query(value = "SELECT new vn.viettel.core.dto.promotion.PromotionProgramDiscountDTO(p.id, p.promotionProgramId, p.minSaleAmount, p.maxSaleAmount," +
            " p.discountAmount, p.discountPercent, p.maxDiscountAmount, p.status, p.discountCode, p.type, p.isUsed, p.orderDate, " +
            " p.orderNumber, p.orderShopCode, p.orderCustomerCode, p.orderAmount, p.actualDiscountAmount, p.customerCode)" +
            " FROM PromotionProgramDiscount p WHERE p.promotionProgramId = :promotionId " +
            " AND p.status = 1 AND p.type IN (0, 3) And (p.isUsed is null or p.isUsed = 0)")
    List<PromotionProgramDiscountDTO> findPromotionDiscountByPromotion(Long promotionId);



    @Query("Select pd from PromotionProgramDiscount pd " +
            " Join PromotionProgram pg ON pg.id = pd.promotionProgramId " +
            " Join PromotionShopMap sm ON sm.promotionProgramId = pg.id " +
            " WHERE pd.discountCode =:discountCode AND pd.status = 1 AND (pd.isUsed IS NULL OR pd.isUsed = 0) " +
            " AND pg.givenType = 2 AND pg.type = 'ZM' AND pg.status = 1 AND sm.shopId =:shopId AND sm.status = 1" +
            " AND pg.fromDate <=:lastDay AND ( pg.toDate IS NULL OR pg.toDate >=:firstDay )" )
    Optional<PromotionProgramDiscount> getPromotionProgramDiscount(String discountCode, Long shopId, LocalDateTime firstDay, LocalDateTime lastDay);
}
