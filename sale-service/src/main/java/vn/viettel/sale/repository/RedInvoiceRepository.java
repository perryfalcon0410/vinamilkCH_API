package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.core.repository.BaseRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

public interface RedInvoiceRepository extends BaseRepository<RedInvoice>, JpaSpecificationExecutor<RedInvoice> {
    @Query(value = "select invoice_number from red_invoices WHERE invoice_number = ?1" , nativeQuery = true)
    String checkRedInvoice(String redInvoiceCode);

    @Query(value = "select INVOICE_NUMBER from red_invoices WHERE id = ?1" , nativeQuery = true)
    String findRedInvoiceNumberById(Long idRedInvoice);

    @Query(value =  "SELECT *\n" +
                    "FROM   red_invoices red_in \n" +
                    "WHERE  red_in.id IN (SELECT regexp_substr(:ids,'[^,]+', 1, level)\n" +
                    "                            FROM dual\n" +
                    "                            CONNECT BY regexp_substr(:ids,'[^,]+', 1, level) IS NOT NULL)" , nativeQuery = true)
    List<RedInvoice> getRedInvoiceByIds(String ids);

    RedInvoice findRedInvoiceById(Long Id);


}
