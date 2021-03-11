package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POAdjustedDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface POAdjustedDetailRepository extends BaseRepository<POAdjustedDetail> {
    @Query(value = "SELECT distinct pad.* FROM po_adjusted_detail pad " +
            "join po_adjusted pa on pa.id = pad.po_adjusted_id " +
            " where 1 = 1 and is_free_item = 1 and pad.po_adjusted_id = :paId", nativeQuery = true)
    List<POAdjustedDetail> getListProductDiscountPoAdjustedDetail(Long paId);

    @Query(value = "SELECT distinct pad.* FROM po_adjusted_detail pad " +
            "join po_adjusted pa on pa.id = pad.po_adjusted_id " +
            " where 1 = 1 and is_free_item = 0 and pad.po_adjusted_id = :paId", nativeQuery = true)
    List<POAdjustedDetail> getListProductPoAdjustedDetail(Long paId);

    @Query(value = "SELECT distinct pad.* FROM po_adjusted_detail pad " +
            "join po_adjusted pa on pa.id = pad.po_adjusted_id " +
            " where 1 = 1 and pa.po_license_number = :poNo", nativeQuery = true)
    List<POAdjustedDetail> getListPOAdjustedDetail(String poNo);

    @Query(value = "SELECT distinct pad.* FROM po_adjusted_detail pad " +
            "join po_adjusted pa on pa.id = pad.po_adjusted_id " +
            "join receiptimport reci on reci.po_number = pa.po_license_number " +
            " where 1 = 1 and pa.po_license_number = :poNumber", nativeQuery = true)
    List<POAdjustedDetail> getPOAdjustedDetailByPoNumber(String poNumber);

    List<POAdjustedDetail> findAllByPoAdjustedId(Long  paID);
}
