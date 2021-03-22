package vn.viettel.saleservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.POAdjusted;
import vn.viettel.core.db.entity.ReceiptExport;
import vn.viettel.core.db.entity.ReceiptOnline;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface POAdjustedRepository extends BaseRepository<POAdjusted> {
    POAdjusted findByPoAdjustedNumber(String poNo);

    @Query(value = "SELECT * FROM PO_ADJUSTEDS WHERE PO_TYPE =1 and ID =:poId  " , nativeQuery = true)
    POAdjusted findAdjustedExport(Long poId );

    @Query(value = "SELECT * FROM PO_ADJUSTEDS WHERE PO_TYPE =1", nativeQuery = true)
    Page<POAdjusted> getAdjustedExport (Pageable pageable);


}
