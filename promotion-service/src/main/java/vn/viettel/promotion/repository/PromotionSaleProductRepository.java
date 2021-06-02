package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionSaleProduct;

import java.util.List;

public interface PromotionSaleProductRepository extends BaseRepository<PromotionSaleProduct> {
    @Query(value = "SELECT * FROM PROMOTION_SALE_PRODUCT WHERE PRODUCT_ID = :ID", nativeQuery = true)
    List<PromotionSaleProduct> getPromotionSaleProductsByProductId(long ID);

    List<PromotionSaleProduct> findByProductId(long id);

    List<PromotionSaleProduct> findByPromotionProgramIdAndStatus(Long id, Integer status);
}
