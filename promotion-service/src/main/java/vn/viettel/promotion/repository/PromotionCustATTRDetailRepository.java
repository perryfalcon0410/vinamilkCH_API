package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionCustATTRDetail;

import java.util.List;


public interface PromotionCustATTRDetailRepository extends BaseRepository<PromotionCustATTRDetail> {
    @Query(value = "SELECT PCAD.OBJECT_ID FROM PROMOTION_CUST_ATTR_DETAIL PCAD " +
            "JOIN PROMOTION_CUST_ATTR PCA ON PCA.ID = PCAD.PROMOTION_CUST_ATTR_ID " +
            "WHERE PCA.OBJECT_TYPE=2 AND PCA.STATUS =1 AND PCAD.STATUS =1 AND PCA.PROMOTION_PROGRAM_ID =:programId ", nativeQuery = true)
    List<Long> getCusTypeIdByProgramId(Long programId);

    @Query(value = "SELECT PCAD.OBJECT_ID FROM PROMOTION_CUST_ATTR_DETAIL PCAD " +
            "JOIN PROMOTION_CUST_ATTR PCA ON PCA.ID = PCAD.PROMOTION_CUST_ATTR_ID " +
            "WHERE PCA.OBJECT_TYPE=4 AND PCA.STATUS =1 AND PCAD.STATUS =1 AND PCA.PROMOTION_PROGRAM_ID =:programId ", nativeQuery = true)
    List<Long> getMemberCardIdByProgramId(Long programId);

    @Query(value = "SELECT PCAD.OBJECT_ID FROM PROMOTION_CUST_ATTR_DETAIL PCAD " +
            "JOIN PROMOTION_CUST_ATTR PCA ON PCA.ID = PCAD.PROMOTION_CUST_ATTR_ID " +
            "WHERE PCA.OBJECT_TYPE=5 AND PCA.STATUS =1 AND PCAD.STATUS =1 AND PCA.PROMOTION_PROGRAM_ID =:programId ", nativeQuery = true)
    List<Long> getCusLoyalByProgramId(Long programId);

    @Query(value = "SELECT PCAD.OBJECT_ID FROM PROMOTION_CUST_ATTR_DETAIL PCAD " +
            "JOIN PROMOTION_CUST_ATTR PCA ON PCA.ID = PCAD.PROMOTION_CUST_ATTR_ID " +
            "WHERE PCA.OBJECT_TYPE=6 AND PCA.STATUS =1 AND PCAD.STATUS =1 AND PCA.PROMOTION_PROGRAM_ID =:programId ", nativeQuery = true)
    List<Long> getCusCardByProgramId(Long programId);
}
