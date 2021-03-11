package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POBorrowDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface POBorrowDetailRepository extends BaseRepository<POBorrowDetail>{
    @Query(value = "SELECT distinct pbd.* FROM po_borow_detail pbd " +
            "join po_borow pa on pb.id = pbd.po_borow_id " +
            " where 1 = 1 and is_free_item = 1 and pad.po_borow_id = :paId", nativeQuery = true)
    List<POBorrowDetail> getListProductPromotional(Long paId);

    @Query(value = "SELECT distinct pbd.* FROM po_borow_detail pbd " +
            "join po_borow pa on pb.id = pbd.po_borow_id " +
            " where 1 = 1 and is_free_item = 0 and pad.po_borow_id = :paId", nativeQuery = true)
    List<POBorrowDetail> getListProduct(Long paId);

    @Query(value = "SELECT distinct pbd.* FROM po_borrow_detail pbd " +
            "join po_borrow pb on pb.id = pbd.po_borrow_id " +
            " where 1 = 1 and pb.po_borrow_number = :poNo", nativeQuery = true)
    List<POBorrowDetail> getListPoBorrowDetail(String poNo);

    @Query(value = "SELECT distinct pbd.* FROM po_borrow_detail pbd " +
            "join po_borrow pb on pb.id = pad.po_borrow_id " +
            "join receiptimport reci on reci.po_number = pb.po_borrow_number " +
            " where 1 = 1 and pb.po_borrow_number = :poNumber", nativeQuery = true)
    List<POBorrowDetail> getPOBorrowDetailByPoNumber(String poNumber);

    List<POBorrowDetail> findAllByPoBorrowId(Long pbId);
}
