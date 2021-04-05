package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.promotion.PromotionCustATTR;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PromotionCusAttrRepository extends BaseRepository<PromotionCustATTR> {
    @Query(value = "SELECT * FROM PROMOTION_CUST_ATTR WHERE PROMOTION_PROGRAM_ID IN :ids", nativeQuery = true)
    List<PromotionCustATTR> getListCustomerAttributed(List<Long> ids);
}