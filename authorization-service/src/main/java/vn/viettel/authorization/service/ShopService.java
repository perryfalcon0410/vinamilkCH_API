package vn.viettel.authorization.service;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.ShopParamRequest;

import java.util.Map;

public interface ShopService {
    ShopDTO getById(Long id);
    ShopDTO getByShopCode(String code);
    ShopDTO getByName(String name);
    Boolean isEditableOnlineOrder(Long shopId);
    Boolean isManuallyCreatableOnlineOrder(Long shopId);
    String dayReturn(Long id);
    ShopParamDTO getShopParam(String type, String code, Long shopId);
    ShopParamDTO updateShopParam(ShopParamRequest request, Long id);
    ShopParamDTO getImportSaleReturn(Long shopId);
    /*
        Danh sách shop = map sd để ko call db nhiều lần
     */
    Map<Integer, ShopDTO> getAllShopToRedInvoice();

}
