package vn.viettel.authorization.service;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.messaging.ShopParamRequest;

public interface ShopService {
    Response<ShopDTO> getById(Long id);
    Response<ShopDTO> getByName(String name);
    Response<Boolean> isEditableOnlineOrder(Long shopId);
    Response<Boolean> isManuallyCreatableOnlineOrder(Long shopId);
    Response<String> dayReturn(Long id);
    ShopParamDTO getShopParam(String type, String code, Long shopId);
    ShopParamDTO updateShopParam(ShopParamRequest request, Long id);

}
