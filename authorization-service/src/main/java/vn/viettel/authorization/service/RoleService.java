package vn.viettel.authorization.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.dto.RoleDTO;
import vn.viettel.core.db.entity.Role;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface RoleService extends BaseService {

    Role getByRoleName(UserRole userRole);

    Role getById(Long id);

    Response<List<RoleDTO>> getListRole();
}
