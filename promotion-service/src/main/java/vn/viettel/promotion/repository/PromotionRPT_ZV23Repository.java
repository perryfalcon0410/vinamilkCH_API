package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.RPT_ZV23;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PromotionRPT_ZV23Repository extends BaseRepository<RPT_ZV23> {
    @Query(value = "SELECT * FROM RPT_ZV23 zv" +
            " JOIN PROMOTION_PROGRAM pr ON pr.ID = zv.PROMOTION_PROGRAM_ID" +
            " WHERE zv.PROMOTION_PROGRAM_ID =:promotionId" +
            " AND zv.CUSTOMER_ID =:customerId" +
            " AND zv.SHOP_ID =:shopId" +
            " AND zv.FROM_DATE <= :useDate" +
            " AND zv.TO_DATE >= :useDate" +
            " ORDER BY zv.CREATED_AT DESC, zv.UPDATED_AT DESC", nativeQuery = true)
    List<RPT_ZV23> checkZV23Require(Long promotionId, Long customerId, Long shopId, Date useDate);
}


