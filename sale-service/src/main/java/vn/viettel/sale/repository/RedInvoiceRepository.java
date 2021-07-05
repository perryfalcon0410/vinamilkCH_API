package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.messaging.TotalRedInvoice;

import java.time.LocalDateTime;
import java.util.List;

public interface RedInvoiceRepository extends BaseRepository<RedInvoice>, JpaSpecificationExecutor<RedInvoice> {

    @Query(value = "select invoiceNumber from RedInvoice WHERE invoiceNumber = ?1")
    String checkRedInvoice(String redInvoiceCode);

    RedInvoice findRedInvoiceById(Long Id);

    @Query(value = "select orderNumbers from RedInvoice WHERE id = ?1")
    String getIdSaleOrder(Long redInvoiceId);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.messaging.TotalRedInvoice(SUM(sbt.totalQuantity), SUM(sbt.totalMoney)) " +
            "FROM   RedInvoice sbt " +
            "WHERE  1 = 1 " +
            "       AND (coalesce(:customerIds, null) IS NULL OR sbt.customerId in :customerIds ) " +
            "       AND (:invoiceNo IS NULL OR upper(sbt.invoiceNumber) LIKE %:invoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND ( sbt.printDate BETWEEN :fromDate AND :toDate ) " +
            "")
    TotalRedInvoice getTotalRedInvoice1(Long shopId, List<Long> customerIds, String invoiceNo, LocalDateTime fromDate, LocalDateTime toDate);

    @Query(value = "" +
            "SELECT NEW vn.viettel.sale.messaging.TotalRedInvoice(SUM(dtl.amountNotVat), SUM(dtl.amount - dtl.amountNotVat) ) " +
            "FROM   RedInvoice sbt JOIN RedInvoiceDetail dtl ON sbt.id = dtl.redInvoiceId " +
            "WHERE  1 = 1 " +
            "       AND (coalesce(:customerIds, null) IS NULL OR sbt.customerId in :customerIds ) " +
            "       AND (:invoiceNo IS NULL OR upper(sbt.invoiceNumber) LIKE %:invoiceNo% ) " +
            "       AND (:shopId IS NULL OR sbt.shopId = :shopId ) " +
            "       AND ( sbt.printDate BETWEEN :fromDate AND :toDate ) " +
            "")
    TotalRedInvoice getTotalRedInvoice2(Long shopId, List<Long> customerIds, String invoiceNo, LocalDateTime fromDate, LocalDateTime toDate);
}
