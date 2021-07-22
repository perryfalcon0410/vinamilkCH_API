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
import vn.viettel.core.messaging.ShopParamRequest;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /*
    Nếu cửa hàng không khai báo thì lấy cấu hình theo cấp cha của cửa hàng đó
     */
    @Override
    public String dayReturn(Long id) {
        Shop shop = repository.findByIdAndStatus(id, 1).orElseThrow(() -> new ValidateException(ResponseMessage.SHOP_NOT_FOUND));
        String day = shopParamRepo.dayReturn(id);
        if(day == null && shop.getParentShopId()!=null) day = shopParamRepo.dayReturn(shop.getParentShopId());
        if(day == null) day = "-1";
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

    @Override
    public Long getLevelUpdateCustomer(Long shopId) {
        String level = repository.getLevelUpdateCustomer(shopId);
        Long result = 0L;
        if(!level.isEmpty()){
            result = Long.parseLong(level);
        }
        return result;
    }

    @Override
    public Map<Integer, ShopDTO> getAllShopToRedInvoice() {
        List<Shop> shops = repository.findAll();
        Map<Integer, ShopDTO> shopDTOS = new HashMap<>();
        for(Shop shop : shops)
        {
            ShopDTO shopDTO = modelMapper.map(shop, ShopDTO.class);
            Integer id = Math.toIntExact(shopDTO.getId());
            if(id != null)
            shopDTOS.put(id, shopDTO);
        }
        return shopDTOS;
    }
}
