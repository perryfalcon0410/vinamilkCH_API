package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.repository.ManagementPrivilegeRepository;
import vn.viettel.authorization.service.ManagementPrivilegeService;
import vn.viettel.core.db.entity.ManagementPrivilege;
import vn.viettel.core.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagementPrivilegeServiceImpl extends BaseServiceImpl<ManagementPrivilege, ManagementPrivilegeRepository> implements ManagementPrivilegeService {
    @Override
    public ManagementPrivilege getById(Long id) {
        Optional<ManagementPrivilege> managementPrivilege = repository.getById(id);
        return managementPrivilege.orElse(null);
    }
}
