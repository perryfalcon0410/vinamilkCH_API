package vn.viettel.report.repository;

import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.repository.BaseRepository;

public interface ShopRepository extends BaseRepository<Shop> {
    Shop findByShopName(String name);
}
