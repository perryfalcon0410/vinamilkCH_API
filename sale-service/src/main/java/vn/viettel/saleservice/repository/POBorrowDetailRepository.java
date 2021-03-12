package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.POBorrowDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface POBorrowDetailRepository extends BaseRepository<POBorrowDetail>{
    @Query(value = "SELECT distinct pbd.* FROM PO_BOROW_DETAILS pbd " +
            "join PO_BOROWS pa on pb.ID = pbd.PO_BOROW_ID " +
            " where 1 = 1 and IS_FREE_ITEM = 1 and pad.PO_BOROW_ID = :paId", nativeQuery = true)
    List<POBorrowDetail> getListProductPromotional(Long paId);

    @Query(value = "SELECT distinct pbd.* FROM PO_BOROW_DETAILS pbd " +
            "join PO_BOROWS pa on pb.ID = pbd.PO_BOROW_ID " +
            " where 1 = 1 and IS_FREE_ITEM = 0 and pad.PO_BOROW_ID = :paId", nativeQuery = true)
    List<POBorrowDetail> getListProduct(Long paId);

    @Query(value = "SELECT distinct pbd.* FROM PO_BORROW_DETAILS pbd " +
            "join PO_BORROWS pb on pb.ID = pbd.PO_BORROW_ID " +
            " where 1 = 1 and pb.PO_BORROW_NUMBER = :poNo", nativeQuery = true)
    List<POBorrowDetail> getListPoBorrowDetail(String poNo);

    @Query(value = "SELECT distinct pbd.* FROM PO_BORROW_DETAILS pbd " +
            "join PO_BORROWS pb on pb.ID = pad.PO_BORROW_ID " +
            "join RECEIPT_IMPORTS reci on reci.PO_NUMBER = pb.PO_BORROW_NUMBER " +
            " where 1 = 1 and pb.PO_BORROW_NUMBER = :poNumber", nativeQuery = true)
    List<POBorrowDetail> getPOBorrowDetailByPoNumber(String poNumber);

    List<POBorrowDetail> findAllByPoBorrowId(Long pbId);
}
