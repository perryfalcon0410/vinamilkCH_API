package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.sale.service.dto.RedInvoiceDetailDTO;

import java.math.BigDecimal;
import java.util.List;

public interface RedInvoiceDetailRepository extends BaseRepository<RedInvoiceDetail> {

    @Query(value = "SELECT dtl FROM RedInvoiceDetail dtl WHERE dtl.redInvoiceId =:id")
    List<RedInvoiceDetail> getAllByRedInvoiceId(Long id);

    @Query(value = "SELECT dtl.id FROM RedInvoiceDetail dtl WHERE dtl.redInvoiceId =:id")
    List<BigDecimal> getAllRedInvoiceIds(Long id);
}