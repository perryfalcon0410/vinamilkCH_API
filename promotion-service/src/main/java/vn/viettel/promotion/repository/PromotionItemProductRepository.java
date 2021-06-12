package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionItemProduct;

import java.util.List;

public interface PromotionItemProductRepository extends BaseRepository<PromotionItemProduct> {
    @Query(value = "SELECT pi.PRODUCT_ID FROM PROMOTION_ITEM_PRODUCT pi" +
            " JOIN PROMOTION_ITEM_GROUP pg ON pi.PROMOTION_ITEM_GROUP_ID = pg.ID" +
            " WHERE pg.NOT_ACCUMULATED = 1 AND pg.STATUS = 1" +
            " AND pi.STATUS = 1" +
            " AND pi.PRODUCT_ID IN :productIds", nativeQuery = true)
    List<Long> productsNotAccumulated(List<Long> productIds);
}
