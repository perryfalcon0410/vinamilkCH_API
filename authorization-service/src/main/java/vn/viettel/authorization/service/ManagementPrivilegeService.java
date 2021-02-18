package vn.viettel.authorization.service;

import vn.viettel.core.db.entity.ManagementPrivilege;
import vn.viettel.core.service.BaseService;


public interface ManagementPrivilegeService extends BaseService {
    ManagementPrivilege getById(Long id);
}
