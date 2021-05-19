package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PoTransRepository extends BaseRepository<PoTrans>, JpaSpecificationExecutor<PoTrans> {
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE TYPE = 1 ", nativeQuery = true)
    int getQuantityPoTrans();
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE TO_CHAR(TRANS_DATE,'YYYY') = TO_CHAR(SYSDATE,'YYYY') AND TYPE = 2 ", nativeQuery = true)
    int getQuantityPoTransExport();
    PoTrans getPoTransById(Long transId);
    @Query(value = "SELECT * FROM PO_TRANS WHERE TYPE = 2 AND PO_ID IS NOT NULL AND TRANS_CODE LIKE  %'IMP' ", nativeQuery = true)
    Page<PoTrans> getPoTransImportNotPromotion(Specification<PoTrans> and, Pageable pageable);
    @Query(value = "SELECT RED_INVOICE_NO FROM PO_TRANS WHERE TYPE = 1 AND STATUS =1 ", nativeQuery = true)
    List<String> getRedInvoiceNo();
    @Query(value = "SELECT PO_NUMBER FROM PO_TRANS WHERE TYPE = 1 AND STATUS =1 ", nativeQuery = true)
    List<String> getPoNumber();
    Optional<PoTrans> getByTransCodeAndStatus(String transCode, Integer status);
}
