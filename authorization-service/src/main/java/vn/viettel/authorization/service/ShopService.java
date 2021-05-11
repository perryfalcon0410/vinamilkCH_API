package vn.viettel.authorization.service;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.messaging.Response;

public interface ShopService {
    Response<ShopDTO> getById(Long id);
    Response<ShopDTO> getByName(String name);
    Response<Boolean> isEditableOnlineOrder(Long shopId);
    Response<Boolean> isManuallyCreatableOnlineOrder(Long shopId);
    Response<String> dayReturn(Long id);
}
