package vn.viettel.saleservice.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.ReceiptImport;
import vn.viettel.core.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptImportRepository extends BaseRepository<ReceiptImport> {
    @Query(value = "SELECT *  FROM receiptimport where receipt_date >= fromdate and receipt_date < toDate+1" +
            "and invoice_number LIKE '%invoiceNumber%' and receipt_type = type", nativeQuery = true)
    List<ReceiptImport> getReceiptImportByVariable (LocalDateTime fromDate, LocalDateTime toDate, String invoiceNumber, Integer type);

    @Query(value = "SELECT COUNT(id) FROM receiptimport", nativeQuery = true)
    int getReceiptImportNumber();
}
