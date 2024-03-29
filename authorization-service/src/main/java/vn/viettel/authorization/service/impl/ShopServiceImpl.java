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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl extends BaseServiceImpl<Shop, ShopRepository> implements ShopService {
    @Autowired
    ShopParamRepository shopParamRepo;

    @Override
    public ShopDTO getById(Long id) {
        Shop shop = repository.findById(id).orElse(null);
        if(shop == null) return null;
        ShopDTO shopDTO = modelMapper.map(shop, ShopDTO.class);
        if(shop.getParentShopId()!=null) {
            Shop shopP = repository.findById(shop.getParentShopId()).orElse(null);
            if(shopP != null) shopDTO.setParentShop(modelMapper.map(shopP, ShopDTO.class));
        }
        return shopDTO;
    }

    @Override
    public ShopDTO getByShopCode(String code) {
        Shop shop = repository.findByShopCode(code);
        if(shop == null) return null;
        return modelMapper.map(shop, ShopDTO.class);
    }

    @Override
    public ShopDTO getByName(String name) {
        return modelMapper.map(repository.findByShopName(name), ShopDTO.class);
    }

    @Override
    public Boolean isEditableOnlineOrder(Long shopId) {
        String type = "SALEMT_ONLINE_ORDER";
        String code = "VES_EDITING";
        List<Shop> shops = getParentShop(shopId);
        ShopParam param = shopParamRepo.getShopParam(type, code, shopId).orElse(null);
        if(param == null){
            for (Shop shop : shops){
                param = shopParamRepo.getShopParam(type, code, shop.getId()).orElse(null);
                if(param != null) break;
            }
        }
        if(param == null) return false;
        return true;
    }

    @Override
    public Boolean isManuallyCreatableOnlineOrder(Long shopId) {
        String type = "SALEMT_ONLINE_ORDER";
        String code = "MANUAL_ORDER";
        List<Shop> shops = getParentShop(shopId);
        ShopParam param = shopParamRepo.getShopParam(type, code, shopId).orElse(null);
        if(param == null){
            for (Shop shop : shops){
                param = shopParamRepo.getShopParam(type, code, shop.getId()).orElse(null);
                if(param != null) break;
            }
        }
        if(param == null) return false;
        return true;
    }

    /*
    Nếu cửa hàng không khai báo thì lấy cấu hình theo cấp cha của cửa hàng đó, shop cha và con ko có thì ko dc phép trả hàng
    Nếu kết quả
    --+ VALUE = -1 ; Không được trả hàng
    --+ VALUE = 0 : Chỉ được trả hàng cho ngày hiện tại
    --+ VALUE = n , n >0 thì được trả hàng trong n ngày
    => Kiểm tra trunc(sale_order.order_date) >= trunc(sysdate) - số ngày cấu hình
     */
    @Override
    public String dayReturn(Long shopId) {
        String type = "SALEMT_TH";
        String code = "NTKH";
        List<Shop> shops = getParentShop(shopId);
        ShopParam param = shopParamRepo.dayReturn(shopId, type, code);
        if(param == null){
            for (Shop shop : shops){
                param = shopParamRepo.dayReturn(shop.getId(), type, code);
                if(param != null) break;
            }
        }

        if(param == null || param.getName() == null || param.getName().isEmpty()) return "-1";

        return param.getName();
    }

    private List<Shop> getParentShop(Long shopId){
        List<Shop> lstResults = new ArrayList<>();
        if(shopId != null){
            Shop shop = repository.getById(shopId);
            if(shop != null && shop.getStatus() == 1) {
                shopId = shop.getParentShopId();
                for (int i = 0;;i++){
                    if(shopId == null) break;
                    Shop shop1 = repository.getById(shopId);
                    if(shop1 == null || shop1.getStatus() != 1) break;
                    lstResults.add(shop1);
                    shopId = shop1.getParentShopId();
                }
            }
        }

        return lstResults;
    }

    @Override
    public ShopParamDTO getShopParam(String type, String code, Long shopId) {
        Optional<ShopParam> shopParam = shopParamRepo.getShopParam(type, code, shopId);
//            .orElseThrow(() -> new ValidateException(ResponseMessage.SHOP_PARAM_NOT_FOUND));
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
    public String getImportSaleReturn(Long shopId) {
        String type = "SALEMT_LIMIT_DAY_RETURN";
        String code = "IMPORT_TRANS_RETURN";
        List<Shop> shops = getParentShop(shopId);
        ShopParam param = shopParamRepo.dayReturn(shopId, type, code);
        if(param == null){
            for (Shop shop : shops){
                param = shopParamRepo.dayReturn(shop.getId(), type, code);
                if(param != null) break;
            }
        }

        if(param == null || param.getName() == null || param.getName().isEmpty()) return "1";

        return param.getName();
    }

    @Override
    public Long getLevelUpdateCustomer(Long shopId) {
        String type = "SALEMT_EDIT_CUSTOMER";
        String code = "OTHER_STORE";
        List<Shop> shops = getParentShop(shopId);
        ShopParam param = shopParamRepo.dayReturn(shopId, type, code);
        if(param == null){
            for (Shop shop : shops){
                param = shopParamRepo.dayReturn(shop.getId(), type, code);
                if(param != null) break;
            }
        }

        if(param == null || param.getName() == null || param.getName().isEmpty()) return 0L;

        return Long.parseLong(param.getName());
    }

    @Override
    public List<ShopDTO> getAllShopToRedInvoice(List<Long> shopIds) {
        if(shopIds == null || shopIds.isEmpty()) return new ArrayList<>();
        List<Shop> shops = repository.getShopByIds(shopIds);
        List<ShopDTO> shopDTOS =  shops.stream().map(s -> modelMapper.map(s, ShopDTO.class)).collect(Collectors.toList());

        return shopDTOS;
    }
    
}
