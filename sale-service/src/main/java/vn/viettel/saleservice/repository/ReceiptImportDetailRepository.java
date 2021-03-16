package vn.viettel.saleservice.repository;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptImportDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface ReceiptImportDetailRepository extends BaseRepository<ReceiptImportDetail> {
    List<ReceiptImportDetail> findByReceiptImportId (Long reciId);
}
