package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ReceiptOnlineRepository extends BaseRepository<ReceiptOnline>, JpaSpecificationExecutor<ReceiptOnline> {
    ReceiptOnline findByReceiptCode(String code);
    List<ReceiptOnline> findByStatus(int status);

    @Query(value = "SELECT * FROM RECEIPT_ONLINES WHERE STATUS = :status " +
            "AND CREATED_AT BETWEEN :fromDate AND :toDate", nativeQuery = true)
    List<ReceiptOnline> findByStatusAndCreatedAtBetween(int status, String fromDate, String toDate);
}
