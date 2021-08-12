package vn.viettel.promotion.repository;

import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionSaleProduct;

import java.util.List;

public interface PromotionSaleProductRepository extends BaseRepository<PromotionSaleProduct> {

    List<PromotionSaleProduct> findByPromotionProgramIdAndStatus(Long promotionProgramId, Integer status);
}
