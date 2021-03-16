package vn.viettel.saleservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptExportAdjusted;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface ReceiptExportAdjustedRepository extends BaseRepository<ReceiptExportAdjusted> {
    @Query(value = "SELECT * FROM RECEIPT_EXPORT_ADJUSTEDS WHERE STATUS = 0 " , nativeQuery = true)
    Page<ReceiptExportAdjusted> getAll ( Pageable pageable);
}
