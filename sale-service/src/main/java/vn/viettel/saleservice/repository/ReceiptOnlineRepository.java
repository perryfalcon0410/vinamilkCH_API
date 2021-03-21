package vn.viettel.saleservice.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.repository.BaseRepository;

public interface ReceiptOnlineRepository extends BaseRepository<ReceiptOnline>, JpaSpecificationExecutor<ReceiptOnline> {
}
