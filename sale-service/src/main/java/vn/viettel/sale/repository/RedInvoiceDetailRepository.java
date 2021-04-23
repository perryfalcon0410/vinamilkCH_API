package vn.viettel.sale.repository;

import vn.viettel.sale.entities.RedInvoiceDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface RedInvoiceDetailRepository extends BaseRepository<RedInvoiceDetail> {
    Optional<RedInvoiceDetail> getByRedInvoiceId(Long id);
}
