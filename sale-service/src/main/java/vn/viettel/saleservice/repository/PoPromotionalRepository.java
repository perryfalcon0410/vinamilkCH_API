package vn.viettel.saleservice.repository;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.PoPromotional;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface PoPromotionalRepository extends BaseRepository<PoPromotional> {
    PoPromotional findPoPromotionalByPoPromotionalNumber(String po);
}
