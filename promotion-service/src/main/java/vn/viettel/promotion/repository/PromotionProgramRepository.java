package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgram;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionProgramRepository extends BaseRepository<PromotionProgram> {
    @Query(value = "SELECT DISTINCT p FROM PromotionProgram p JOIN PromotionShopMap ps ON p.id = ps.promotionProgramId " +
            " WHERE p.status = 1 AND p.fromDate <=:lastDay AND ( p.toDate IS NULL OR p.toDate >=:firstDay ) " +
            " AND ps.status = 1 AND ps.shopId IN :shopIds " +
            " AND ps.fromDate <=:lastDay AND ( ps.toDate IS NULL OR ps.toDate >=:firstDay )")
    List<PromotionProgram> findAvailableProgram(List<Long> shopIds, LocalDateTime firstDay, LocalDateTime lastDay);

    @Query(value = "SELECT pp FROM PromotionProgram pp " +
                    "WHERE pp.promotionProgramCode = :code")
    PromotionProgram findByCode(String code);

    PromotionProgram findByPromotionProgramCode(String code);

    /*
    Lấy km cho đơn hàng
    - 1. Kiểm tra các CTKM đang hoạt động
    - 3. Kiểm tra loại đơn hàng tham gia
    - 4. Kiểm tra thuộc tính khách hàng tham gia
     */
    @Query(value = "SELECT DISTINCT p " +
            " FROM PromotionProgram p " +
            " JOIN PromotionShopMap ps ON p.id = ps.promotionProgramId " +
            " WHERE p.status = 1 AND p.fromDate <=:lastDay AND ( p.toDate IS NULL OR p.toDate >=:firstDay ) " +
            " AND ps.status = 1 AND ps.shopId IN :shopIds " +
            " AND ps.fromDate <=:lastDay AND ( ps.toDate IS NULL OR ps.toDate >=:firstDay ) " +
            // 3. Kiểm tra loại đơn hàng tham gia
            " AND ( p.objectType is null OR p.objectType = 0 OR p.id IN (" +
            "       SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA " +
            "       JOIN PromotionCustATTRDetail PCAD ON PCA.id = PCAD.promotionCustAttrId AND PCAD.status =1 " +
            "       WHERE PCA.objectType = 20 AND PCA.status =1 AND PCAD.objectId =:orderType ) " +
            "     ) " +
            /* 4.	Kiểm tra thuộc tính khách hàng tham gia
            -	Nếu CTKM không quy định trên thuộc tính nào thì hiểu là thuộc tính đó không sử dụng
            -	Quy định giữa các thuộc tính khác hàng là xét điều kiện AND, nội dung quy định trong 1 thuộc tính là điều kiện OR
             */
            " AND ( " + // nếu không quy định	Loại khách hàng
            "        NOT EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA WHERE PCA.objectType = 2 AND PCA.status = 1 AND PCA.promotionProgramId = p.id ) " +
            "      OR " + // nếu có quy định	Loại khách hàng
            "        EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA " +
            "                 JOIN PromotionCustATTRDetail PCAD ON PCA.id = PCAD.promotionCustAttrId AND PCAD.status =1 " +
            "                 WHERE PCA.objectType = 2 AND PCA.status = 1 AND PCAD.objectId =:customerTypeId AND PCA.promotionProgramId = p.id " +
            "               ) " +
            "     ) " +
            " AND ( " + // nếu không quy định	Thẻ thành viên
            "        NOT EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA WHERE PCA.objectType = 4 AND PCA.status = 1 AND PCA.promotionProgramId = p.id ) " +
            "      OR " + // nếu có quy định	Thẻ thành viên
            "        EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA " +
            "                 JOIN PromotionCustATTRDetail PCAD ON PCA.id = PCAD.promotionCustAttrId AND PCAD.status =1 " +
            "                 WHERE PCA.objectType = 4 AND PCA.status = 1 AND PCAD.objectId =:memberCardId AND PCA.promotionProgramId = p.id " +
            "               ) " +
            "     ) " +
            " AND ( " + // nếu không quy định	Loại KH thân thiết
            "        NOT EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA WHERE PCA.objectType = 5 AND PCA.status = 1 AND PCA.promotionProgramId = p.id ) " +
            "      OR " + // nếu có quy định	Loại KH thân thiết
            "        EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA " +
            "                 JOIN PromotionCustATTRDetail PCAD ON PCA.id = PCAD.promotionCustAttrId AND PCAD.status =1 " +
            "                 WHERE PCA.objectType = 5 AND PCA.status = 1 AND PCAD.objectId =:cusCloselyTypeId AND PCA.promotionProgramId = p.id " +
            "               ) " +
            "     ) " +
            " AND ( " + // nếu không quy định	Loại thẻ KH
            "        NOT EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA WHERE PCA.objectType = 6 AND PCA.status = 1 AND PCA.promotionProgramId = p.id ) " +
            "      OR " + // nếu có quy định	Loại thẻ KH
            "        EXISTS ( SELECT DISTINCT PCA.promotionProgramId FROM PromotionCustATTR PCA " +
            "                 JOIN PromotionCustATTRDetail PCAD ON PCA.id = PCAD.promotionCustAttrId AND PCAD.status =1 " +
            "                 WHERE PCA.objectType = 6 AND PCA.status = 1 AND PCAD.objectId =:cusCardTypeId AND PCA.promotionProgramId = p.id " +
            "               ) " +
            "     ) " +
            "")
    List<PromotionProgram> findProgramWithConditions(List<Long> shopIds, Long orderType, Long customerTypeId, Long memberCardId, Long cusCloselyTypeId
                                                     ,Long cusCardTypeId, LocalDateTime firstDay, LocalDateTime lastDay);
}
