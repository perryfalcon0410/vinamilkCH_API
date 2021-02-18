package vn.viettel.authorization.service;

import vn.viettel.core.db.entity.PrivilegesUsers;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface PrivilegesUserService extends BaseService {
    List<PrivilegesUsers> getPrivilegesByManagementUserId(Long id);

    Boolean deleteByUserId(Long id);

    PrivilegesUsers getByPrivilegeIdAndUserId(Long idPrivige, Long idUser);
}
