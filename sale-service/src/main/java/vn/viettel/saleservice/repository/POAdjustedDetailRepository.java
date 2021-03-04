package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POAdjustedDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface POAdjustedDetailRepository extends BaseRepository<POAdjustedDetail> {
    @Query(value = "SELECT pad.* FROM po_adjusted_detail pad " +
            "join po_adjusted pa on pa.id = pad.po_adjusted_id " +
            " where 1 = 1 and is_free_item = 1 and pad.po_adjusted_id = paId", nativeQuery = true)
    List<POAdjustedDetail> getListProductDiscountPoAdjustedDetail(Long paId);

    @Query(value = "SELECT pad.* FROM po_adjusted_detail pad " +
            "join po_adjusted pa on pa.id = pad.po_adjusted_id " +
            " where 1 = 1 and is_free_item = 0 and pad.po_adjusted_id = paId", nativeQuery = true)
    List<POAdjustedDetail> getListProductPoAdjustedDetail(Long paId);
}
