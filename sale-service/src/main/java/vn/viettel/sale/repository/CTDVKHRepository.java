package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.CTDVKH;
import vn.viettel.sale.entities.HddtExcel;

import java.util.List;

public interface CTDVKHRepository extends JpaRepository<CTDVKH, Long> {
    @Query(value =  "SELECT detail.id, detail.shop_id, red_in.invoice_number, pro.product_code, pro.uom1, detail.quantity\n" +
            "FROM   red_invoice_details detail\n" +
            "JOIN   red_invoices red_in on red_in.id = detail.red_invoice_id\n" +
            "JOIN   products pro on pro.id = detail.product_id\n" +
            "WHERE  red_in.id IN (SELECT regexp_substr(:ids,'[^,]+', 1, level)\n" +
            "                        FROM dual\n" +
            "                         CONNECT BY regexp_substr(:ids,'[^,]+', 1, level) IS NOT NULL)" , nativeQuery = true)
    List<CTDVKH> getCTDVKHByIds(String ids);
}
