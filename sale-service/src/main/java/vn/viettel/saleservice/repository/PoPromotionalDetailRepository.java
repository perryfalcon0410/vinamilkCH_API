package vn.viettel.saleservice.repository;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.PoPromotionalDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface PoPromotionalDetailRepository extends BaseRepository<PoPromotionalDetail> {
    List<PoPromotionalDetail> findPoPromotionalDetailsByPoPromotionalId (Long poId);
}
