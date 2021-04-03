package vn.viettel.sale.service;

import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface ApParamService extends BaseService {
    Response<ApParam> getApParamById(Long id);
    Response<List<ApParam>> getAll();
}
