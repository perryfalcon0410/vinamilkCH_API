package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.viettel.sale.entities.HddtExcel;

import java.util.List;

public interface HDDTExcelRepository extends JpaRepository<HddtExcel, Long> {

    @Query(value =  "SELECT detail.id, red_in.buyerName, red_in.officeWorking, red_in.officeAddress, " +
            "       red_in.taxCode, red_in.shopId, red_in.customerId, red_in.paymentType, " +
            "       red_in.orderNumbers, pro.productCode, pro.productName, pro.uom1, detail.quantity, " +
            "       detail.priceNotVat, detail.price, red_in.note " +
            "FROM   RedInvoice red_in " +
            "JOIN   RedInvoiceDetail detail on red_in.id = detail.redInvoiceId " +
            "JOIN   Product pro on pro.id = detail.productId " +
            "WHERE  coalesce(:ids, null) is null or red_in.id IN (:ids) ")
    List<HddtExcel> getDataHddtExcel(List<Long> ids);
}
