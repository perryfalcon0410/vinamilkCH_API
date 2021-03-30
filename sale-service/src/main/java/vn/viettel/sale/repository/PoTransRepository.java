package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.repository.BaseRepository;

import javax.persistence.Entity;
import javax.persistence.NamedNativeQuery;
import java.util.List;


public interface PoTransRepository extends BaseRepository<PoTrans>, JpaSpecificationExecutor<PoTrans> {
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE TO_CHAR(TRANS_DATE,'YYYY') = TO_CHAR(SYSDATE,'YYYY') ", nativeQuery = true)
    int getQuantityPoTrans();
    PoTrans getPoTransByIdAndDeletedAtIsNull(Long transId);
    @Query(nativeQuery = true)
    Page<PoTrans> getAllByKeyWords(Pageable pageable);
}
