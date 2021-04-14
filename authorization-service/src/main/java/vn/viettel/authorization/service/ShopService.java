package vn.viettel.authorization.service;

import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.messaging.Response;

public interface ShopService {
    Response<Shop> getById(Long id);
    Response<Shop> getByName(String name);
}
