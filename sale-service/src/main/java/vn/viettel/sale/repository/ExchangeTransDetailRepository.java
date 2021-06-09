package vn.viettel.sale.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.sale.entities.ExchangeTransDetail;
import vn.viettel.core.repository.BaseRepository;
import java.util.List;

public interface ExchangeTransDetailRepository extends BaseRepository<ExchangeTransDetail> {
    List<ExchangeTransDetail> findByTransId(Long id);
}
