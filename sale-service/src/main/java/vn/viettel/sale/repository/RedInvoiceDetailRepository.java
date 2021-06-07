package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;

import java.math.BigDecimal;
import java.util.List;

public interface RedInvoiceDetailRepository extends BaseRepository<RedInvoiceDetail> {
    @Query(value = "SELECT * FROM RED_INVOICE_DETAILS WHERE RED_INVOICE_ID =:id", nativeQuery = true)
    List<RedInvoiceDetail> getAllByRedInvoiceId(Long id);

    @Query(value = "SELECT id FROM RED_INVOICE_DETAILS WHERE red_invoice_id =:id", nativeQuery = true)
    List<BigDecimal> getAllRedInvoiceIds(Long id);


}