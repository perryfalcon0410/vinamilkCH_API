package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface RedInvoiceDetailRepository extends BaseRepository<RedInvoiceDetail> {
    @Query(value = "SELECT * FROM RED_INVOICE_DETAILS WHERE RED_INVOICE_ID =:id", nativeQuery = true)
    List<RedInvoiceDetail> getAllByRedInvoiceId(Long id);
}