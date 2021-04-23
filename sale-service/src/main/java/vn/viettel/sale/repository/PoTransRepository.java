package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;


@Repository
public interface PoTransRepository extends BaseRepository<PoTrans>, JpaSpecificationExecutor<PoTrans> {
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE DELETED_AT IS NULL AND TYPE = 1 ", nativeQuery = true)
    int getQuantityPoTrans();
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE TO_CHAR(TRANS_DATE,'YYYY') = TO_CHAR(SYSDATE,'YYYY') AND TYPE = 2 ", nativeQuery = true)
    int getQuantityPoTransExport();
    PoTrans getPoTransByIdAndDeletedAtIsNull(Long transId);
    @Query(value = "SELECT * FROM PO_TRANS WHERE TYPE = 2 AND PO_ID IS NOT NULL AND TRANS_CODE LIKE  %'IMP' ", nativeQuery = true)
    Page<PoTrans> getPoTransImportNotPromotion(Specification<PoTrans> and, Pageable pageable);

    Optional<PoTrans> getPoTransByTransCodeAndDeletedAtIsNull(String transCode);
}
