package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.sale.entities.HddtExcel;

import java.util.List;

public interface HDDTExcelRepository extends JpaRepository<HddtExcel, Long> {

    @Query(value =  "SELECT red_in.id, red_in.buyer_name, red_in.office_working, red_in.office_address,\n" +
            "       red_in.tax_code, red_in.shop_id, red_in.customer_id, red_in.payment_type,\n" +
            "       red_in.order_number, pro.product_code, pro.product_name, pro.uom1, detail.quantity,\n" +
            "       detail.price_not_vat, detail.price, red_in.note\n" +
            "FROM   red_invoices red_in\n" +
            "JOIN   red_invoice_details detail on red_in.id = detail.red_invoice_id\n" +
            "JOIN   products pro on pro.id = detail.product_id\n" +
            "WHERE  red_in.id IN (SELECT regexp_substr(:ids,'[^,]+', 1, level)\n" +
            "                            FROM dual\n" +
            "                             CONNECT BY regexp_substr(:ids,'[^,]+', 1, level) IS NOT NULL)" , nativeQuery = true)
    List<HddtExcel> getDataHddtExcel(String ids);
}
