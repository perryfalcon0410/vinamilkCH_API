package vn.viettel.authorization.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.authorization.entities.Shop;
import vn.viettel.authorization.entities.ShopParam;
import vn.viettel.authorization.repository.ShopParamRepository;
import vn.viettel.authorization.repository.ShopRepository;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

@Service
public class ShopServiceImpl extends BaseServiceImpl<Shop, ShopRepository> implements ShopService {
    @Autowired
    ShopParamRepository shopParamRepo;

    @Override
    public Response<ShopDTO> getById(Long id) {
        Response<ShopDTO> response = new Response<>();
        return response.withData(modelMapper.map(repository.findById(id).get(), ShopDTO.class));
    }

    @Override
    public Response<ShopDTO> getByName(String name) {
        Response<ShopDTO> response = new Response<>();
        return response.withData(modelMapper.map(repository.findByShopName(name), ShopDTO.class));
    }

    @Override
    public Response<Boolean> isEditableOnlineOrder(Long shopId) {
        ShopParam shopParam = shopParamRepo.isEditable(shopId);
        if(shopParam == null)
            return new Response<Boolean>().withData(false);
        return new Response<Boolean>().withData(true);
    }

    @Override
    public Response<Boolean> isManuallyCreatableOnlineOrder(Long shopId) {
        ShopParam shopParam = shopParamRepo.isManuallyCreatable(shopId);
        if(shopParam == null)
            return new Response<Boolean>().withData(false);
        return new Response<Boolean>().withData(true);
    }

    @Override
    public Response<String> dayReturn(Long id) {
        String day = shopParamRepo.dayReturn(id);
        return new Response<String>().withData(day);
    }
}
