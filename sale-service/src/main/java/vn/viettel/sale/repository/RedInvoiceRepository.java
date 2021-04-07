package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.sale.RedInvoice;
import vn.viettel.core.db.entity.stock.PoTrans;
import vn.viettel.core.repository.BaseRepository;

public interface RedInvoiceRepository extends BaseRepository<RedInvoice>, JpaSpecificationExecutor<RedInvoice> {
}
