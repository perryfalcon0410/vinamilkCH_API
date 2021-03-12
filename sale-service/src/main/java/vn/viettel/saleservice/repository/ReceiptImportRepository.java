package vn.viettel.saleservice.repository;


import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

@Repository
public interface ReceiptImportRepository extends BaseRepository<ReceiptImport> {
    @Query(value = "SELECT *  FROM RECEIPT_IMPORTS where RECEIPT_IMPORT_DATE between fromDate and toDate " +
            "and INVOICE_NUMBER LIKE '%invoiceNumber%' and RECEIPT_IMPORT_TYPE = type", nativeQuery = true)
    List<ReceiptImport> getReceiptImportByVariable (String fromDate, String toDate, String invoiceNumber, Integer type);

    @Query(value = "SELECT COUNT(ID) FROM RECEIPT_IMPORT", nativeQuery = true)
    int getReceiptImportNumber();


}
