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
    @Query(value = "SELECT MAX(TRANS_CODE) FROM PO_TRANS WHERE TYPE = 1 ", nativeQuery = true)
    String getQuantityPoTrans();
    @Query(value = "SELECT COUNT(ID) FROM PO_TRANS WHERE TO_CHAR(TRANS_DATE,'YYYY') = TO_CHAR(SYSDATE,'YYYY') AND TYPE = 2 ", nativeQuery = true)
    int getQuantityPoTransExport();
    PoTrans getPoTransById(Long transId);
    @Query(value = "SELECT redInvoiceNo FROM PoTrans WHERE type = 1 AND status =1 ")
    List<String> getRedInvoiceNo();
    @Query(value = "SELECT pocoNumber FROM PoTrans WHERE type = 1 AND status =1 ", nativeQuery = true)
    List<String> getPoCoNumber();

    @Query(value = "SELECT internalNumber FROM PoTrans WHERE type = 1 AND status =1 ", nativeQuery = true)
    List<String> getInternalNumber();

}
