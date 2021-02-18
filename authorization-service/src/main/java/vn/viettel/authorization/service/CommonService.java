package vn.viettel.authorization.service;

import vn.viettel.authorization.service.dto.group.GroupDTO;
import vn.viettel.authorization.service.dto.shop.ShopDTO;
import vn.viettel.core.service.BaseService;

public interface CommonService extends BaseService {
    ShopDTO getShopBy(Long shopId);
    GroupDTO getGroupBy(Long groupId);
}
