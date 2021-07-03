package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.CTDVKH;
import vn.viettel.sale.entities.HddtExcel;

import java.util.List;

public interface CTDVKHRepository extends JpaRepository<CTDVKH, Long> {
    @Query(value =  "SELECT detail.id, detail.shopId, red_in.invoiceNumber, pro.productCode, pro.uom1, detail.quantity " +
            "FROM   RedInvoiceDetail detail " +
            "JOIN   RedInvoice red_in on red_in.id = detail.redInvoiceId " +
            "JOIN   Product pro on pro.id = detail.productId " +
            "WHERE  coalesce(:ids, null) is null or red_in.id IN (:ids) ")
    List<CTDVKH> getCTDVKHByIds(List<Long> ids);
}
