package vn.viettel.saleservice.repository;


import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptImportRepository extends BaseRepository<ReceiptImport> {
    @Query(value = "SELECT *  FROM receiptimport where receipt_date between fromDate and toDate " +
            "and invoice_number LIKE '%invoiceNumber%' and receipt_type = type", nativeQuery = true)
    List<ReceiptImport> getReceiptImportByVariable (String fromDate, String toDate, String invoiceNumber, Integer type);

    @Query(value = "SELECT COUNT(id) FROM receiptimport", nativeQuery = true)
    int getReceiptImportNumber();


}
