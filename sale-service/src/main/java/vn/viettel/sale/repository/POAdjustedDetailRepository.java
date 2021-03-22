package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POAdjustedDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface POAdjustedDetailRepository extends BaseRepository<POAdjustedDetail> {
    @Query(value = "SELECT distinct pad.* FROM PO_ADJUSTED_DETAILS pad " +
            "join PO_ADJUSTEDS pa on pa.ID = pad.PO_ADJUSTED_ID " +
            " where 1 = 1 and IS_FREE_ITEM = 1 and pad.PO_ADJUSTED_ID = :paId", nativeQuery = true)
    List<POAdjustedDetail> getListProductDiscountPoAdjustedDetail(Long paId);

    @Query(value = "SELECT distinct pad.* FROM PO_ADJUSTED_DETAILS pad " +
            "join PO_ADJUSTEDS pa on pa.ID = pad.PO_ADJUSTED_ID " +
            " where 1 = 1 and IS_FREE_ITEM = 0 and pad.PO_ADJUSTED_ID = :paId", nativeQuery = true)
    List<POAdjustedDetail> getListProductPoAdjustedDetail(Long paId);

    @Query(value = "SELECT distinct pad.* FROM PO_ADJUSTED_DETAILS pad " +
            "join PO_ADJUSTEDS pa on pa.ID = pad.PO_ADJUSTED_ID " +
            " where 1 = 1 and pa.PO_ADJUSTED_DETAIL_NUMBER = :poNo", nativeQuery = true)
    List<POAdjustedDetail> getListPOAdjustedDetail(String poNo);

    @Query(value = "SELECT distinct pad.* FROM PO_ADJUSTED_DETAILS pad " +
            "join PO_ADJUSTEDS pa on pa.id = pad.PO_ADJUSTED_ID " +
            "join RECEIPT_IMPORTS reci on reci.PO_NUMBER = pa.PO_ADJUSTED_DETAIL_NUMBER " +
            " where 1 = 1 and pa.PO_ADJUSTED_DETAIL_NUMBER = :poNumber", nativeQuery = true)
    List<POAdjustedDetail> getPOAdjustedDetailByPoNumber(String poNumber);

    List<POAdjustedDetail> findAllByPoAdjustedId(Long  paID);
    @Query(value = "SELECT SUM(QUANTITY) FROM PO_ADJUSTED_DETAILS where PO_ADJUSTED_ID = :poId " , nativeQuery = true)
    Integer sumAllQuantityPoAdjustedDetailByPoId(Long poId);
    @Query(value = "SELECT SUM(PRICE_TOTAL) FROM PO_ADJUSTED_DETAILS where PO_ADJUSTED_ID = :poId " , nativeQuery = true)
    Float sumAllPriceTotalPoAdjustedDetailByPoId(Long poId);
}
