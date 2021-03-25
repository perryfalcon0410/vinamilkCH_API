package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.common.Price;
import vn.viettel.core.repository.BaseRepository;

public interface ProductPriceRepository extends BaseRepository<Price> {
    Price findByProductId(Long id);
}
