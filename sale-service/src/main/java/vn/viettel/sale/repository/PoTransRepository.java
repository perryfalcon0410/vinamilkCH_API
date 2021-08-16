package vn.viettel.sale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import vn.viettel.sale.entities.PoTrans;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.messaging.TotalResponse;
import vn.viettel.sale.service.dto.ReceiptImportDTO;
import vn.viettel.sale.service.dto.ReceiptImportListDTO;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PoTransRepository extends BaseRepository<PoTrans>, JpaSpecificationExecutor<PoTrans> {

   @Lock(LockModeType.PESSIMISTIC_WRITE )
   @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
   @Query(value = "SELECT p FROM PoTrans p " +
           " WHERE p.createdAt>= :startDate And p.type =:type AND p.transCode like :startWith% " +
           " ORDER BY p.transCode desc ")
   Page<PoTrans> getLastTransCode(Integer type, String startWith, LocalDateTime startDate, Pageable pageable);

    @Query(value = "SELECT pt FROM PoTrans pt WHERE pt.type = 1 AND pt.redInvoiceNo =:redInvoiceNo AND pt.status =1 ")
    List<PoTrans> getByRedInvoiceNo(String redInvoiceNo);

   @Query(value = "SELECT pt FROM PoTrans pt WHERE pt.type = 1 AND pt.pocoNumber =:pocoNumber AND pt.status =1 ")
   List<PoTrans> getByPoCoNumber(String pocoNumber);

    @Query(value = "SELECT pt FROM PoTrans pt WHERE pt.type = 1 AND pt.internalNumber = :internalNumber AND pt.status =1 ")
    List<PoTrans> getByInternalNumber(String internalNumber);

    @Query(value = "" +
            "SELECT pot.ID              AS id,              pot.TRANS_CODE      AS transCode,       pot.RED_INVOICE_NO  AS redInvoiceNo, " +
            "       pot.INTERNAL_NUMBER AS internalNumber,  pot.TOTAL_QUANTITY  AS totalQuantity,   pot.TOTAL_AMOUNT    AS totalAmount, " +
            "       pot.TRANS_DATE      AS transDate,       pot.NOTE            AS note,            pot.PO_ID           AS poId, 0 AS receiptType " +
            "FROM   PO_TRANS pot " +
            "WHERE  pot.STATUS = 1 AND pot.TYPE = :type " +
            "       AND (:transCode IS NULL OR UPPER(pot.TRANS_CODE) LIKE '%'||UPPER(TRIM (:transCode))||'%' ) " +
            "       AND (:redInvoiceNo IS NULL OR UPPER(pot.RED_INVOICE_NO) LIKE '%'||UPPER (TRIM(:redInvoiceNo))||'%' ) " +
            "       AND (:shopId IS NULL OR pot.SHOP_ID = :shopId ) " +
            "       AND ( pot.TRANS_DATE BETWEEN :fromDate AND :toDate ) " +
            "UNION " +
            "SELECT sat.ID              AS id,              sat.TRANS_CODE      AS transCode,       sat.RED_INVOICE_NO  AS redInvoiceNo, " +
            "       sat.INTERNAL_NUMBER AS internalNumber,  sat.TOTAL_QUANTITY  AS totalQuantity,   sat.TOTAL_AMOUNT    AS totalAmount, " +
            "       sat.TRANS_DATE      AS transDate,       sat.NOTE            AS note,            null                AS poId, 1 AS receiptType " +
            "FROM   STOCK_ADJUSTMENT_TRANS sat " +
            "WHERE  sat.STATUS = 1 AND sat.TYPE = :type " +
            "       AND (:transCode IS NULL OR UPPER(sat.TRANS_CODE) LIKE '%'||UPPER(TRIM (:transCode))||'%' ) " +
            "       AND (:redInvoiceNo IS NULL OR UPPER(sat.RED_INVOICE_NO) LIKE '%'||UPPER (TRIM(:redInvoiceNo))||'%' ) " +
            "       AND (:shopId IS NULL OR sat.SHOP_ID = :shopId ) " +
            "       AND ( sat.TRANS_DATE BETWEEN :fromDate AND :toDate ) " +
            "UNION " +
            "SELECT sbt.ID              AS id,              sbt.TRANS_CODE      AS transCode,       sbt.RED_INVOICE_NO  AS redInvoiceNo, " +
            "       sbt.INTERNAL_NUMBER AS internalNumber,  sbt.TOTAL_QUANTITY  AS totalQuantity,   sbt.TOTAL_AMOUNT    AS totalAmount, " +
            "       sbt.TRANS_DATE      AS transDate,       sbt.NOTE            AS note,            null                AS poId, 2 AS receiptType " +
            "FROM   STOCK_BORROWING_TRANS sbt " +
            "WHERE  sbt.STATUS = 1 AND sbt.TYPE = :type " +
            "       AND (:transCode IS NULL OR UPPER(sbt.TRANS_CODE) LIKE '%'||UPPER(TRIM (:transCode))||'%' ) " +
            "       AND (:redInvoiceNo IS NULL OR UPPER(sbt.RED_INVOICE_NO) LIKE '%'||UPPER (TRIM(:redInvoiceNo))||'%' ) " +
            "       AND (:shopId IS NULL OR sbt.SHOP_ID = :shopId ) " +
            "       AND ( sbt.TRANS_DATE BETWEEN :fromDate AND :toDate ) " +
            "ORDER BY transDate desc, transCode" +
            "",
            countQuery = "SELECT COUNT(*) FROM (" +
                    "SELECT pot.ID              AS id,              pot.TRANS_CODE      AS transCode,       pot.RED_INVOICE_NO  AS redInvoiceNo, " +
                    "       pot.INTERNAL_NUMBER AS internalNumber,  pot.TOTAL_QUANTITY  AS totalQuantity,   pot.TOTAL_AMOUNT    AS totalAmount, " +
                    "       pot.TRANS_DATE      AS transDate,       pot.NOTE            AS note,            pot.PO_ID           AS poId, 0 AS receiptType " +
                    "FROM   PO_TRANS pot " +
                    "WHERE  pot.STATUS = 1 AND pot.TYPE = :type " +
                    "       AND (:transCode IS NULL OR UPPER(pot.TRANS_CODE) LIKE '%'||UPPER(TRIM (:transCode))||'%' ) " +
                    "       AND (:redInvoiceNo IS NULL OR UPPER(pot.RED_INVOICE_NO) LIKE '%'||UPPER (TRIM(:redInvoiceNo))||'%' ) " +
                    "       AND (:shopId IS NULL OR pot.SHOP_ID = :shopId ) " +
                    "       AND ( pot.TRANS_DATE BETWEEN :fromDate AND :toDate ) " +
                    "UNION " +
                    "SELECT sat.ID              AS id,              sat.TRANS_CODE      AS transCode,       sat.RED_INVOICE_NO  AS redInvoiceNo, " +
                    "       sat.INTERNAL_NUMBER AS internalNumber,  sat.TOTAL_QUANTITY  AS totalQuantity,   sat.TOTAL_AMOUNT    AS totalAmount, " +
                    "       sat.TRANS_DATE      AS transDate,       sat.NOTE            AS note,            null                AS poId, 1 AS receiptType " +
                    "FROM   STOCK_ADJUSTMENT_TRANS sat " +
                    "WHERE  sat.STATUS = 1 AND sat.TYPE = :type " +
                    "       AND (:transCode IS NULL OR UPPER(sat.TRANS_CODE) LIKE '%'||UPPER(TRIM (:transCode))||'%' ) " +
                    "       AND (:redInvoiceNo IS NULL OR UPPER(sat.RED_INVOICE_NO) LIKE '%'||UPPER (TRIM(:redInvoiceNo))||'%' ) " +
                    "       AND (:shopId IS NULL OR sat.SHOP_ID = :shopId ) " +
                    "       AND ( sat.TRANS_DATE BETWEEN :fromDate AND :toDate ) " +
                    "UNION " +
                    "SELECT sbt.ID              AS id,              sbt.TRANS_CODE      AS transCode,       sbt.RED_INVOICE_NO  AS redInvoiceNo, " +
                    "       sbt.INTERNAL_NUMBER AS internalNumber,  sbt.TOTAL_QUANTITY  AS totalQuantity,   sbt.TOTAL_AMOUNT    AS totalAmount, " +
                    "       sbt.TRANS_DATE      AS transDate,       sbt.NOTE            AS note,            null                AS poId, 2 AS receiptType " +
                    "FROM   STOCK_BORROWING_TRANS sbt " +
                    "WHERE  sbt.STATUS = 1 AND sbt.TYPE = :type " +
                    "       AND (:transCode IS NULL OR UPPER(sbt.TRANS_CODE) LIKE '%'||UPPER(TRIM (:transCode))||'%' ) " +
                    "       AND (:redInvoiceNo IS NULL OR UPPER(sbt.RED_INVOICE_NO) LIKE '%'||UPPER (TRIM(:redInvoiceNo))||'%' ) " +
                    "       AND (:shopId IS NULL OR sbt.SHOP_ID = :shopId ) " +
                    "       AND ( sbt.TRANS_DATE BETWEEN :fromDate AND :toDate ) " +
                    "ORDER BY transDate desc, transCode" +
                    ")",
            nativeQuery = true)
    Page<ReceiptImportDTO> getReceipt(Long shopId, int type, String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.service.dto.ReceiptImportListDTO( sbt.id, sbt.transCode, sbt.redInvoiceNo," +
            "       sbt.internalNumber,  sbt.totalQuantity, sbt.totalAmount, sbt.transDate, sbt.note, 0, sbt.poId ) " +
            "FROM   PoTrans sbt " +
            "WHERE  sbt.status = 1 " +
            "       AND  sbt.type=:type " +
            "       AND (:transCode IS NULL OR upper(sbt.transCode) LIKE %:transCode% ) " +
            "       AND (:redInvoiceNo IS NULL OR upper(sbt.redInvoiceNo) LIKE %:redInvoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND sbt.transDate BETWEEN :fromDate AND :toDate " +
            "ORDER BY sbt.transDate desc, sbt.transCode " +
            "")
    Page<ReceiptImportListDTO> getReceiptPo(Long shopId, Integer type, String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.messaging.TotalResponse(SUM(sbt.totalQuantity), SUM(sbt.totalAmount)) " +
            "FROM   PoTrans sbt " +
            "WHERE  sbt.status = 1 AND sbt.type = :type " +
            "       AND (:transCode IS NULL OR upper(sbt.transCode) LIKE %:transCode% ) " +
            "       AND (:redInvoiceNo IS NULL OR upper(sbt.redInvoiceNo) LIKE %:redInvoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND ( sbt.transDate BETWEEN :fromDate AND :toDate ) " +
            "")
    TotalResponse getTotalResponsePo(Long shopId, int type, String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.service.dto.ReceiptImportListDTO( sbt.id, sbt.transCode, sbt.redInvoiceNo," +
            "       sbt.internalNumber,  sbt.totalQuantity, sbt.totalAmount, sbt.transDate, sbt.note, 1 ) " +
            "FROM   StockAdjustmentTrans sbt " +
            "WHERE  sbt.status = 1 AND sbt.type = :type " +
            "       AND (:transCode IS NULL OR upper(sbt.transCode) LIKE %:transCode% ) " +
            "       AND (:redInvoiceNo IS NULL OR upper(sbt.redInvoiceNo) LIKE %:redInvoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND ( sbt.transDate BETWEEN :fromDate AND :toDate ) " +
            "ORDER BY sbt.transDate desc, sbt.transCode " +
            "")
    Page<ReceiptImportListDTO> getReceiptAdjustment(Long shopId, int type, String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.messaging.TotalResponse(SUM(sbt.totalQuantity), SUM(sbt.totalAmount)) " +
            "FROM   StockAdjustmentTrans sbt " +
            "WHERE  sbt.status = 1 AND sbt.type = :type " +
            "       AND (:transCode IS NULL OR upper(sbt.transCode) LIKE %:transCode% ) " +
            "       AND (:redInvoiceNo IS NULL OR upper(sbt.redInvoiceNo) LIKE %:redInvoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND (sbt.transDate BETWEEN :fromDate AND :toDate ) " +
            "")
    TotalResponse getTotalResponseAdjustment(Long shopId, int type, String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.service.dto.ReceiptImportListDTO( sbt.id, sbt.transCode, sbt.redInvoiceNo," +
            "       sbt.internalNumber,  sbt.totalQuantity, sbt.totalAmount, sbt.transDate, sbt.note, 2 ) " +
            "FROM   StockBorrowingTrans sbt " +
            "WHERE  sbt.status = 1 AND sbt.type = :type " +
            "       AND (:transCode IS NULL OR upper(sbt.transCode) LIKE %:transCode% ) " +
            "       AND (:redInvoiceNo IS NULL OR upper(sbt.redInvoiceNo) LIKE %:redInvoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND (  sbt.transDate BETWEEN :fromDate AND :toDate ) " +
            "       AND ( sbt.transDate BETWEEN :fromDate AND :toDate ) " +
            "ORDER BY sbt.transDate desc, sbt.transCode " +
            "")
    Page<ReceiptImportListDTO> getReceiptBorrowing(Long shopId, int type, String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.messaging.TotalResponse(SUM(sbt.totalQuantity), SUM(sbt.totalAmount)) " +
            "FROM   StockBorrowingTrans sbt " +
            "WHERE  sbt.status = 1 AND sbt.type = :type " +
            "       AND (:transCode IS NULL OR upper(sbt.transCode) LIKE %:transCode% ) " +
            "       AND (:redInvoiceNo IS NULL OR upper(sbt.redInvoiceNo) LIKE %:redInvoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND ( sbt.transDate BETWEEN :fromDate AND :toDate ) " +
            "")
    TotalResponse getTotalResponseBorrowing(Long shopId, int type, String transCode, String redInvoiceNo, LocalDateTime fromDate, LocalDateTime toDate);
}
