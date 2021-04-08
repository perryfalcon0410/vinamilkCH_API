package vn.viettel.customer.service;

import vn.viettel.core.db.entity.authorization.User;
import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CustomerTypeService extends BaseService {
    Response<List<CustomerType>> getAll();
    Response<CustomerType> findById(Long id);
    CustomerType getCusTypeByShopId(long shopId);
}
