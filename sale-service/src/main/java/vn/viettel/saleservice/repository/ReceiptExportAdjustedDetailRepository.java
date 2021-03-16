package vn.viettel.saleservice.repository;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptExportAdjustedDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface ReceiptExportAdjustedDetailRepository extends BaseRepository<ReceiptExportAdjustedDetail> {
    List<ReceiptExportAdjustedDetail> findByReceiptExportAdjustedId (Long Id);
}
