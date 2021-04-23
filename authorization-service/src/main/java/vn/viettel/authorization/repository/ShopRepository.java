package vn.viettel.authorization.repository;

import vn.viettel.authorization.entities.Shop;
import vn.viettel.core.repository.BaseRepository;

public interface ShopRepository extends BaseRepository<Shop> {
    Shop findByShopName(String name);
}
