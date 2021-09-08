package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionSaleProduct;

import java.util.List;

public interface PromotionSaleProductRepository extends BaseRepository<PromotionSaleProduct> {

    List<PromotionSaleProduct> findByPromotionProgramIdAndStatus(Long promotionProgramId, Integer status);

    @Query("Select count(p.id) From PromotionSaleProduct p Where p.promotionProgramId = :promotionProgramId And p.status = 1 And p.productId In (:productIds)")
    Integer findByPromotionProgramIdAndStatus(Long promotionProgramId, List<Long> productIds);

    @Query("Select count(p.id) From PromotionSaleProduct p Where p.promotionProgramId = :promotionProgramId And p.status = 1 ")
    Integer countDetail(Long promotionProgramId);

}
