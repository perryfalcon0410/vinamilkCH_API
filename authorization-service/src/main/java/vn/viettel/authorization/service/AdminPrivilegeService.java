package vn.viettel.authorization.service;

import vn.viettel.core.db.entity.AdminPrivilege;
import vn.viettel.core.service.BaseService;

public interface AdminPrivilegeService extends BaseService {
    AdminPrivilege getAdminPrivilege(Long id);
}
