package vn.viettel.authorization.service;

import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.ShopParamDTO;
import vn.viettel.core.messaging.ShopParamRequest;

import java.util.List;
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
    String getImportSaleReturn(Long shopId);
    /*
        Lấy level chỉnh sửa customer của shop theo id
     */
    Long getLevelUpdateCustomer(Long shopId);

    /*
        Danh sách shop = map sd để ko call db nhiều lần
     */
    List<ShopDTO> getAllShopToRedInvoice(List<Long> shopIds);

}
