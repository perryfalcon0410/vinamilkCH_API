package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.PoPromotionalDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface PoPromotionalDetailRepository extends BaseRepository<PoPromotionalDetail> {
    List<PoPromotionalDetail> findPoPromotionalDetailsByPoPromotionalId (Long poId);
    @Query(value = "SELECT SUM(QUANTITY) FROM PO_PROMOTIONAL_DETAILS where PO_PROMOTIONAL_ID = :poId " , nativeQuery = true)
    Integer sumAllQuantityPoPromotionDetailByPoId(Long poId);
    @Query(value = "SELECT SUM(PRICE_TOTAL) FROM PO_PROMOTIONAL_DETAILS where PO_PROMOTIONAL_ID = :poId " , nativeQuery = true)
    Float sumAllPriceTotalPromotionDetailByPoId(Long poId);
}
