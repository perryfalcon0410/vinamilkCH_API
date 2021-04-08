package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.stock.ExchangeTransDetail;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ExchangeTransDetailRepository extends BaseRepository<ExchangeTransDetail> {
    List<ExchangeTransDetail> findByTransId(Long id);
}
