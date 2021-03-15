package vn.viettel.saleservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptExport;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface ReceiptExportRepository extends BaseRepository<ReceiptExport> {
    @Query(value = "SELECT rex.*  FROM RECEIPT_EXPORTS rex where (:fromDate is null or rex.RECEIPT_EXPORT_DATE >=:fromDate ) " +
            " and (:toDate is null or rex.RECEIPT_EXPORT_DATE <:fromDate + 1 ) " +
            " and (:invoiceNumber is null or rex.INVOICE_NUMBER LIKE %:invoiceNumber%) " +
            " and (:type is null or rex.RECEIPT_EXPORT_TYPE = :type) ", nativeQuery = true)
    Page<ReceiptExport> getReceiptExportByVariable (@Param("fromDate") String fromDate,
                                                    @Param("toDate") String toDate,
                                                    @Param("invoiceNumber") String invoiceNumber,
                                                    @Param("type") Integer type,
                                                    Pageable pageable);

    @Query(value = "SELECT COUNT(ID) FROM RECEIPT_EXPORTS", nativeQuery = true)
    int getReceiptExportNumber();
}
