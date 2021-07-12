package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.RPT_ZV23;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PromotionRPT_ZV23Repository extends BaseRepository<RPT_ZV23> {
    @Query(value = "SELECT * FROM RPT_ZV23 zv" +
            " JOIN PROMOTION_PROGRAM pr ON pr.ID = zv.PROMOTION_PROGRAM_ID" +
            " WHERE zv.PROMOTION_PROGRAM_CODE =:promotionCode" +
            " AND zv.CUSTOMER_ID =:customerId" +
            " AND zv.SHOP_ID =:shopId" +
            " AND zv.FROM_DATE <= :useDate" +
            " AND zv.TO_DATE >= :useDate" +
            " AND pr.STATUS = 1" +
            " ORDER BY zv.CREATED_AT DESC, zv.UPDATED_AT DESC", nativeQuery = true)
    List<RPT_ZV23> checkZV23Require(String promotionCode, Long customerId, Long shopId, Date useDate);

}


