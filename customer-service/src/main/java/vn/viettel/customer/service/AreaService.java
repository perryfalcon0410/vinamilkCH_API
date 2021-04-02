package vn.viettel.customer.service;

import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface AreaService extends BaseService {
    Response<List<Area>> getAllByType(Integer type);
}
