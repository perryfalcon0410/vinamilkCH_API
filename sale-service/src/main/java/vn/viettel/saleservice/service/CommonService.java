package vn.viettel.saleservice.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.service.dto.ReasonDTO;
import vn.viettel.saleservice.service.dto.ShopDTO;


import java.util.List;

public interface CommonService {
    Response<List<ReasonDTO>> getAllReason();
    Response<ShopDTO> getShopById(long shopId);

}
