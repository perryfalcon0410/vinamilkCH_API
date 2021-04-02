package vn.viettel.customer.service;

import vn.viettel.core.db.entity.common.CustomerType;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CustomerTypeService extends BaseService {
    Response<List<CustomerType>> getAllByStatus(Integer status);
}
