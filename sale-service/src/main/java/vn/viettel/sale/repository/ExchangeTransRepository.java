package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.core.db.entity.stock.ExchangeTrans;
import vn.viettel.core.repository.BaseRepository;

public interface ExchangeTransRepository extends BaseRepository<ExchangeTrans>, JpaSpecificationExecutor<ExchangeTrans> {
}
