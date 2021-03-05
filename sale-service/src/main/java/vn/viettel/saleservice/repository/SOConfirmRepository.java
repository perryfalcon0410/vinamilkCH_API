package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POAdjustedDetail;
import vn.viettel.core.db.entity.SOConfirm;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface SOConfirmRepository extends BaseRepository<SOConfirm> {
    @Query(value = "SELECT distinct so.* FROM so_confirm so " +
            "join po_confirm po on po.id = so.po_confirm_id " +
            " where 1 = 1 and so.is_free_item = 1 and so.po_confirm_id = :paId", nativeQuery = true)
    List<SOConfirm> getListProductPromotional(Long paId);

    @Query(value = "SELECT distinct so.* FROM so_confirm so " +
            "join po_confirm po on po.id = so.po_confirm_id " +
            " where 1 = 1 and so.is_free_item = 0 and so.po_confirm_id = :paId", nativeQuery = true)
    List<SOConfirm> getListProduct(Long paId);

    @Query(value = "SELECT distinct so.* FROM so_confirm so " +
            "join po_confirm po on po.id = so.po_confirm_id " +
            " where 1 = 1 and po.po_no = :poNo", nativeQuery = true)
    List<SOConfirm> getListSoConfirm(String poNo);

    @Query(value = "SELECT distinct so.* FROM so_confirm so " +
            "join po_confirm po on po.id = so.po_confirm_id " +
            "join receiptimport reci on reci.po_number = po.po_no " +
            " where 1 = 1 and po.po_no = :poNumber", nativeQuery = true)
    List<SOConfirm> getSOConfirmByPoNumber(String poNumber);

    @Query(value = "SELECT  * FROM so_confirm so " +
            " where 1 = 1 and so.is_free_item = 0 ", nativeQuery = true)
    List<SOConfirm> getProductSoConfirm0();

    @Query(value = "SELECT  * FROM so_confirm so " +
            " where 1 = 1 and so.is_free_item = 1 ", nativeQuery = true)
    List<SOConfirm> getProductPromotinalSoConfirm1();
}
