package vn.viettel.saleservice.repository;

import org.springframework.stereotype.Repository;
import vn.viettel.core.db.entity.Shop;
import vn.viettel.core.repository.BaseRepository;

@Repository
public interface ShopRepository extends BaseRepository<Shop> {
    Shop getShopById (Long idShop);
}
