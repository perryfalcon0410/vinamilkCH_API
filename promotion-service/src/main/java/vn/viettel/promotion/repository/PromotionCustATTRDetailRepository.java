package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionCustATTRDetail;

import java.util.List;
import java.util.Set;


public interface PromotionCustATTRDetailRepository extends BaseRepository<PromotionCustATTRDetail> {
    @Query(value = "SELECT PCAD.objectId FROM PromotionCustATTRDetail PCAD " +
            "JOIN PromotionCustATTR PCA ON PCA.id = PCAD.promotionCustAttrId " +
            "WHERE PCA.objectType =:objectType AND PCA.status =1 AND PCAD.status =1 AND PCA.promotionProgramId =:programId ")
    Set<Long> getPromotionCustATTRDetail(Long programId, Integer objectType);
}
