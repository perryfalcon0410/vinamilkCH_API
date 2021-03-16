package vn.viettel.saleservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptExportAdjusted;
import vn.viettel.core.db.entity.ReceiptExportBorrow;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface ReceiptExportBorrowRepository extends BaseRepository<ReceiptExportBorrow> {
    @Query(value = "SELECT * FROM RECEIPT_EXPORT_BORROWS WHERE STATUS = 0 " , nativeQuery = true)
    Page<ReceiptExportBorrow> getAll (Pageable pageable);
}
