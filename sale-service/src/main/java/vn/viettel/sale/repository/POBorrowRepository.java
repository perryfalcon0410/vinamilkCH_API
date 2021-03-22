package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.POBorrow;
import vn.viettel.core.repository.BaseRepository;

public interface POBorrowRepository extends BaseRepository<POBorrow> {
    POBorrow findPOBorrowByPoBorrowNumber(String poNo);

    @Query(value = "SELECT * FROM PO_BORROWS WHERE PO_TYPE =1 and ID =:poId  " , nativeQuery = true)
    POBorrow findBorrowExport(Long poId );

    @Query(value = "SELECT * FROM PO_BORROWS WHERE PO_TYPE =1", nativeQuery = true)
    Page<POBorrow> getBorrowExport (Pageable pageable);
}
