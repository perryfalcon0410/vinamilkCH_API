package vn.viettel.authorization.repository;

import vn.viettel.authorization.entities.Shop;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends BaseRepository<Shop> {
    Optional<Shop> findByIdAndStatus(Long id, Integer status);
    Shop findByShopName(String name);
    Shop findByShopCode(String code);

    List<Shop> findByParentShopIdAndShopTypeAndStatus(Long parentId, String type, Integer status);
}
