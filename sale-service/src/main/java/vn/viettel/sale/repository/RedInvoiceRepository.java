package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.core.repository.BaseRepository;

public interface RedInvoiceRepository extends BaseRepository<RedInvoice>, JpaSpecificationExecutor<RedInvoice> {
    @Query(value = "select invoice_number from red_invoices WHERE invoice_number = ?1" , nativeQuery = true)
    String checkRedInvoice(String redInvoiceCode);

    @Query(value = "select id from red_invoices WHERE invoice_number = ?1" , nativeQuery = true)
    Long findIdByRedInvoiceCode(String redInvoiceCode);
}
