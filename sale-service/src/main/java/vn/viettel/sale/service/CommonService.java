package vn.viettel.sale.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.dto.ReasonDTO;
import vn.viettel.sale.service.dto.ShopDTO;


import java.util.List;

public interface CommonService {
    Response<List<ReasonDTO>> getAllReason();
    Response<ShopDTO> getShopById(long shopId);

}
