package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.repository.BaseRepository;
import java.util.List;

public interface RedInvoiceDetailRepository extends BaseRepository<RedInvoiceDetail> {

    @Query(value = "SELECT dtl FROM RedInvoiceDetail dtl WHERE dtl.redInvoiceId =:id")
    List<RedInvoiceDetail> getAllByRedInvoiceId(Long id);

    @Query(value = "SELECT dtl.id FROM RedInvoiceDetail dtl WHERE dtl.redInvoiceId =:id")
    List<Long> getAllRedInvoiceIds(Long id);
}