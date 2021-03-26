//package vn.viettel.sale.repository;
//
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.Query;
//
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import vn.viettel.core.db.entity.ReceiptImport;
//import vn.viettel.core.repository.BaseRepository;
//
//@Repository
//public interface ReceiptImportRepository extends BaseRepository<ReceiptImport> {
//    @Query(value = "SELECT re.*  FROM RECEIPT_IMPORTS re where (:fromDate is null or re.RECEIPT_IMPORT_DATE >=:fromDate ) " +
//            " and (:toDate is null or re.RECEIPT_IMPORT_DATE <:fromDate + 1 ) " +
//            " and (:invoiceNumber is null or re.INVOICE_NUMBER LIKE %:invoiceNumber%) " +
//            " and (:type is null or re.RECEIPT_IMPORT_TYPE = :type) ", nativeQuery = true)
//    Page<ReceiptImport> getReceiptImportByVariable (@Param("fromDate") String fromDate,
//                                                    @Param("toDate") String toDate,
//                                                    @Param("invoiceNumber") String invoiceNumber,
//                                                    @Param("type") Integer type,
//                                                    Pageable pageable);
//
//    @Query(value = "SELECT rex.*  FROM RECEIPT_IMPORTS rex where (:fromDate is null or rex.RECEIPT_IMPORT_DATE >=:fromDate ) " +
//            " and (:toDate is null or rex.RECEIPT_IMPORT_DATE <:fromDate + 1 ) " +
//            " and (:receiptImportCode is null or rex.RECEIPT_IMPORT_CODE LIKE %:receiptImportCode%) " +
//            " and (:invoiceNumber is null or rex.INVOICE_NUMBER LIKE %:invoiceNumber%) " +
//            " and (:internalNumber is null or rex.INTERNAL_NUMBER LIKE %:internalNumber%) " +
//            " and (:poNo is null or rex.PO_NUMBER LIKE %:poNo%) and rex.STATUS in (0,1) " +
//            " and rex.RECEIPT_IMPORT_TYPE in (0,4) ", nativeQuery = true)
//    Page<ReceiptImport> getReceiptImportByAnyVariable (@Param("fromDate") String fromDate,
//                                                       @Param("toDate") String toDate,
//                                                       @Param("receiptImportCode") String receiptImportCode,
//                                                       @Param("invoiceNumber") String invoiceNumber,
//                                                       @Param("internalNumber") String internalNumber,
//                                                       @Param("poNo") String poNo,
//                                                       Pageable pageable);
//    @Query(value = "SELECT COUNT(ID) FROM RECEIPT_IMPORTS", nativeQuery = true)
//    int getReceiptImportNumber();
//
//    @Query(value = "SELECT * FROM RECEIPT_IMPORTS", nativeQuery = true)
//    Page<ReceiptImport> findAll (Pageable pageable);
//
//    ReceiptImport findByPoNumber (String poNumber);
//
//
//}
