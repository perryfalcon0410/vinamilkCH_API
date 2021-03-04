package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POAdjustedDetail;
import vn.viettel.core.db.entity.POBorrowDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface POBorrowDetailRepository extends BaseRepository {
    @Query(value = "SELECT pbd.* FROM po_borow_detail pbd " +
            "join po_borow pa on pb.id = pbd.po_borow_id " +
            " where 1 = 1 and is_free_item = 1 and pad.po_borow_id = paId", nativeQuery = true)
    List<POBorrowDetail> getListProductPromotional(Long paId);

    @Query(value = "SELECT pbd.* FROM po_borow_detail pbd " +
            "join po_borow pa on pb.id = pbd.po_borow_id " +
            " where 1 = 1 and is_free_item = 0 and pad.po_borow_id = paId", nativeQuery = true)
    List<POBorrowDetail> getListProduct(Long paId);
}
