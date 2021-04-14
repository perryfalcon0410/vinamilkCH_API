package vn.viettel.authorization.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.authorization.repository.ShopRepository;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.db.entity.common.Shop;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

@Service
public class ShopServiceImpl extends BaseServiceImpl<Shop, ShopRepository> implements ShopService {
    @Override
    public Response<Shop> getById(Long id) {
        return new Response<Shop>().withData(repository.findById(id).get());
    }

    @Override
    public Response<Shop> getByName(String name) {
        return new Response<Shop>().withData(repository.findByShopName(name));
    }
}
