package vn.viettel.sale.repository;

import vn.viettel.core.db.entity.ProductPrice;
import vn.viettel.core.repository.BaseRepository;

public interface ProductPriceRepository extends BaseRepository<ProductPrice> {
    ProductPrice findByProductId(Long id);
}
