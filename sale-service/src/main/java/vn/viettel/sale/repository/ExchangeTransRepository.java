package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.viettel.sale.entities.ExchangeTrans;
import vn.viettel.core.repository.BaseRepository;

public interface ExchangeTransRepository extends BaseRepository<ExchangeTrans>, JpaSpecificationExecutor<ExchangeTrans> {
}
