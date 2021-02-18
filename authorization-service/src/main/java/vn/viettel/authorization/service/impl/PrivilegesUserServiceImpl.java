package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.repository.PrivilegesUserRepository;
import vn.viettel.authorization.service.PrivilegesUserService;
import vn.viettel.core.db.entity.PrivilegesUsers;
import vn.viettel.core.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrivilegesUserServiceImpl extends BaseServiceImpl<PrivilegesUsers, PrivilegesUserRepository> implements PrivilegesUserService {

    @Override
    public List<PrivilegesUsers> getPrivilegesByManagementUserId(Long id) {
        List<PrivilegesUsers> result = repository.getPrivilegesByManagementUserId(id);
        return result;
    }

    @Override
    public Boolean deleteByUserId(Long id) {
        repository.deleteByUserId(id);
        return true;
    }

    @Override
    public PrivilegesUsers getByPrivilegeIdAndUserId(Long idPrivige, Long idUser) {
        Optional<PrivilegesUsers> usersOptional = repository.getByPrivilegeIdAndUserId(idPrivige,idUser);
        return  usersOptional.orElse(null);
    }

}
