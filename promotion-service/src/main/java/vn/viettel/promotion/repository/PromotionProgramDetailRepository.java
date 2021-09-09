package vn.viettel.promotion.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.dto.promotion.PromotionProgramDetailDTO;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.promotion.entities.PromotionProgramDetail;

import java.util.List;

public interface PromotionProgramDetailRepository extends BaseRepository<PromotionProgramDetail> {

    @Query(value = "SELECT new vn.viettel.core.dto.promotion.PromotionProgramDetailDTO(p.id, p.promotionProgramId," +
            "  p.productId, p.saleQty, p.saleUom, p.saleAmt, p.discAmt," +
            " p.disPer, p.freeProductId, p.freeQty, p.freeUom, p.required, p.salePer, p.orderNumber ) " +
            " FROM PromotionProgramDetail p WHERE p.promotionProgramId = :promotionId " +
            "And (coalesce(:productIds, null) is null OR p.productId In :productIds )" )
    List<PromotionProgramDetailDTO> findByPromotionProgramIdOrderByFreeQtyDesc(Long promotionId, List<Long> productIds);

}
