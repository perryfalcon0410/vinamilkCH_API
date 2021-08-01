package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionItemProduct;

import java.util.List;

public interface PromotionItemProductRepository extends BaseRepository<PromotionItemProduct> {

    @Query(value = "SELECT pi.productId FROM PromotionItemProduct pi" +
            " JOIN PromotionItemGroup pg ON pi.promotionItemGroupId = pg.id " +
            " WHERE pg.notAccumulated = 1 AND pg.status = 1" +
            " AND pi.status = 1" +
            " AND pi.productId IN :productIds")
    List<Long> productsNotAccumulated(List<Long> productIds);
}
