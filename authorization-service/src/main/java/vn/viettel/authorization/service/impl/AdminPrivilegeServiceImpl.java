package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.repository.AdminPrivilegeRepository;
import vn.viettel.authorization.service.AdminPrivilegeService;
import vn.viettel.core.db.entity.AdminPrivilege;
import vn.viettel.core.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminPrivilegeServiceImpl extends BaseServiceImpl<AdminPrivilege, AdminPrivilegeRepository> implements AdminPrivilegeService {
    @Override
    public AdminPrivilege getAdminPrivilege(Long id) {
        Optional<AdminPrivilege> adminPrivilege = repository.getAdminPrivilegeById(id);
        return adminPrivilege.orElse(null);
    }
}
