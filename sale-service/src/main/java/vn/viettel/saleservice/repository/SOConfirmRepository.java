package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.SOConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface SOConfirmRepository extends BaseRepository<SOConfirm> {
    @Query(value = "SELECT distinct so.* FROM SO_CONFIRMS so " +
            "join PO_CONFIRMS po on po.ID = so.PO_CONFIRM_ID " +
            " where 1 = 1 and so.IS_FREE_ITEM = 1 and so.PO_CONFIRM_ID = :paId", nativeQuery = true)
    List<SOConfirm> getListProductPromotional(Long paId);

    @Query(value = "SELECT distinct so.* FROM SO_CONFIRMS so " +
            "join PO_CONFIRMS po on po.ID = so.PO_CONFIRM_ID " +
            " where 1 = 1 and so.IS_FREE_ITEM = 0 and so.PO_CONFIRM_ID = :paId", nativeQuery = true)
    List<SOConfirm> getListProduct(Long paId);

    @Query(value = "SELECT distinct so.* FROM SO_CONFIRMS so " +
            "join PO_CONFIRMS po on po.ID = so.PO_CONFIRM_ID " +
            " where 1 = 1 and po.PO_NO = :poNo", nativeQuery = true)
    List<SOConfirm> getListSoConfirm(String poNo);

    @Query(value = "SELECT distinct so.* FROM SO_CONFIRMS so " +
            "join PO_CONFIRMS po on po.ID = so.PO_CONFIRM_ID " +
            "join RECEIPT_IMPORTS reci on reci.PO_NUMBER = po.po_no " +
            " where 1 = 1 and po.PO_NO = :poNumber", nativeQuery = true)
    List<SOConfirm> getSOConfirmByPoNumber(String poNumber);

    @Query(value = "SELECT  * FROM SO_CONFIRMS so " +
            " where 1 = 1 and so.IS_FREE_ITEM = 0 ", nativeQuery = true)
    List<SOConfirm> getProductSoConfirm0();

    @Query(value = "SELECT  * FROM SO_CONFIRMS so " +
            " where 1 = 1 and so.IS_FREE_ITEM = 1 ", nativeQuery = true)
    List<SOConfirm> getProductPromotinalSoConfirm1();

    @Query(value = "SELECT  * FROM SO_CONFIRMS so " +
            " where 1 = 1 so.PO_CONFIRM_ID = :poId and so.IS_FREE_ITEM = 1 ", nativeQuery = true)
    List<SOConfirm> getListProduct1ByPoId(Long poId);

    List<SOConfirm> findAllByPoConfirmId(Long poId);
    @Query(value = "SELECT SUM(QUANTITY) FROM SO_CONFIRMS where PO_CONFIRM_ID = :poId " , nativeQuery = true)
    Integer sumAllQuantitySoConfirmByPoId(Long poId);
    @Query(value = "SELECT SUM(PRICE_TOTAL) FROM SO_CONFIRMS where PO_CONFIRM_ID = :poId " , nativeQuery = true)
    Float sumAllPriceTotalSoConfirmByPoId(Long poId);
}
