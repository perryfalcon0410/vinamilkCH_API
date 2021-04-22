package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.sale.entities.RedInvoice;
import vn.viettel.core.repository.BaseRepository;

public interface RedInvoiceRepository extends BaseRepository<RedInvoice>, JpaSpecificationExecutor<RedInvoice> {
}
