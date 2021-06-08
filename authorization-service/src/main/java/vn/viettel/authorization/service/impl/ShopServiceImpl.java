package vn.viettel.authorization.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.viettel.authorization.entities.Shop;
import vn.viettel.authorization.entities.ShopParam;
import vn.viettel.authorization.repository.ShopParamRepository;
import vn.viettel.authorization.repository.ShopRepository;
import vn.viettel.authorization.service.ShopService;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.ShopParamRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;

@Service
public class ShopServiceImpl extends BaseServiceImpl<Shop, ShopRepository> implements ShopService {
    @Autowired
    ShopParamRepository shopParamRepo;

    @Override
    public ShopDTO getById(Long id) {
        return modelMapper.map(repository.findById(id).get(), ShopDTO.class);
    }

    @Override
    public ShopDTO getByShopCode(String code) {
        return modelMapper.map(repository.findByShopCode(code), ShopDTO.class);
    }

    @Override
    public ShopDTO getByName(String name) {
        return modelMapper.map(repository.findByShopName(name), ShopDTO.class);
    }

    @Override
    public Boolean isEditableOnlineOrder(Long shopId) {
        ShopParam shopParam = shopParamRepo.isEditable(shopId);
        if(shopParam == null)
            return false;
        return true;
    }

    @Override
    public Boolean isManuallyCreatableOnlineOrder(Long shopId) {
        ShopParam shopParam = shopParamRepo.isManuallyCreatable(shopId);
        if(shopParam == null)
            return false;
        return true;
    }

    @Override
    public String dayReturn(Long id) {
        String day = shopParamRepo.dayReturn(id);
        return day;
    }

    @Override
    public ShopParamDTO getShopParam(String type, String code, Long shopId) {
        ShopParam shopParam = shopParamRepo.getShopParam(type, code, shopId)
            .orElseThrow(() -> new ValidateException(ResponseMessage.SHOP_PARAM_NOT_FOUND));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(shopParam, ShopParamDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShopParamDTO updateShopParam(ShopParamRequest request, Long id) {
        shopParamRepo.findById(id).orElseThrow(() -> new ValidateException(ResponseMessage.SHOP_PARAM_NOT_FOUND));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        request.setId(id);
        ShopParam newShopParam = modelMapper.map(request, ShopParam.class);
        shopParamRepo.save(newShopParam);
        return modelMapper.map(newShopParam, ShopParamDTO.class);
    }

    @Override
    public ShopParamDTO getImportSaleReturn(Long shopId) {
        ShopParam shopParam = shopParamRepo.getImportSaleReturn(shopId);
        if(shopParam == null) throw new ValidateException(ResponseMessage.SHOP_PARAM_NOT_FOUND);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ShopParamDTO shopParamDTO = modelMapper.map(shopParam, ShopParamDTO.class);

        return shopParamDTO;
    }
}
