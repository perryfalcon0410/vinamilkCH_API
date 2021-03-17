package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptExportDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface ReceiptExportDetailRepository extends BaseRepository<ReceiptExportDetail> {
    List<ReceiptExportDetail> findByReceiptExportId (Long Id);
    @Query(value = "SELECT SUM(QUANTITY) FROM RECEIPT_EXPORT_DETAILS", nativeQuery = true)
    Integer sumAllReceiptExport (Long recxId);
}
