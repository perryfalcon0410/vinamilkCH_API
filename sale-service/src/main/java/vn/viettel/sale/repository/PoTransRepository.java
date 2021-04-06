package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.repository.BaseRepository;


@Repository
public interface PoTransRepository extends BaseRepository<PoTrans>, JpaSpecificationExecutor<PoTrans> {
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE TO_CHAR(TRANS_DATE,'YYYY') = TO_CHAR(SYSDATE,'YYYY') AND TYPE = 1 ", nativeQuery = true)
    int getQuantityPoTrans();
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE TO_CHAR(TRANS_DATE,'YYYY') = TO_CHAR(SYSDATE,'YYYY') AND TYPE = 2 ", nativeQuery = true)
    int getQuantityPoTransExport();

    PoTrans getPoTransByIdAndDeletedAtIsNull(Long transId);

    /*@Query(value = "SELECT * FROM PO_TRANS WHERE DELETED_AT IS NOT NULL AND TYPE = 2 AND TRANS_ID = :id  ", nativeQuery = true)
    PoTrans getPoTransExportByIdAndDeletedAtIsNull(Long id);

    @Query(value = "SELECT * FROM PO_TRANS WHERE ID =:id AND TYPE = 1 ", nativeQuery = true)
    PoTrans getPoTransImportById(Long id);*/

    @Query(value = "SELECT * FROM PO_TRANS WHERE DELETED_AT IS NULL AND TYPE = 1 ", nativeQuery = true)
    Page<PoTrans> getPoTransImport(Specification<PoTrans> and, Pageable pageable);

    @Query(value = "SELECT * FROM PO_TRANS WHERE DELETED_AT IS NULL AND TYPE = 2 ", nativeQuery = true)
    Page<PoTrans> getPoTransExport(Specification<PoTrans> and, Pageable pageable);

    @Query(value = "SELECT * FROM PO_TRANS WHERE TYPE = 2 AND PO_ID IS NOT NULL AND TRANS_CODE LIKE  %'IMP' ", nativeQuery = true)
    Page<PoTrans> getPoTransImportNotPromotion(Specification<PoTrans> and, Pageable pageable);

    PoTrans getPoTransByTransCodeAndDeletedAtIsNull(String transCode);
}
