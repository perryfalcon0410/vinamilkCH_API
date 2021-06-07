package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.RPT_ZV23;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRPT_ZV23Repository extends BaseRepository<RPT_ZV23> {
    @Query(value = "SELECT * FROM RPT_ZV23 zv" +
            " JOIN PROMOTION_PROGRAM pr ON pr.ID = zv.PROMOTION_PROGRAM_ID" +
            " WHERE pr.ID =: id" +
            " AND zv.CUSTOMER_ID =:customerId" +
            " AND zv.SHOP_ID =:shopId", nativeQuery = true)
    List<RPT_ZV23> checkZV23Require(Long customerId, Long shopId, Long id);
}


