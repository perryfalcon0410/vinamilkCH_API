package vn.viettel.authorization.service;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.ShopParamRequest;

public interface ShopService {
    ShopDTO getById(Long id);
    ShopDTO getByName(String name);
    Boolean isEditableOnlineOrder(Long shopId);
    Boolean isManuallyCreatableOnlineOrder(Long shopId);
    String dayReturn(Long id);
    ShopParamDTO getShopParam(String type, String code, Long shopId);
    ShopParamDTO updateShopParam(ShopParamRequest request, Long id);
    ShopParamDTO getImportSaleReturn(Long shopId);

}
