package vn.viettel.sale.service;

import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.service.BaseService;

import java.util.Optional;

public interface ApParamService extends BaseService {
    Optional<ApParam> getApParamById(Long id);
}
