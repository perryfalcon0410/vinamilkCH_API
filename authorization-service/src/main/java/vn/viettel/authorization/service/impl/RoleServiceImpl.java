package vn.viettel.authorization.service.impl;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.repository.RoleRepository;
import vn.viettel.authorization.service.RoleService;
import vn.viettel.authorization.service.dto.RoleDTO;
import vn.viettel.core.db.entity.Role;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.service.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends BaseServiceImpl<Role, RoleRepository> implements RoleService {

    @Override
    public Role getByRoleName(UserRole userRole) {
        Optional<Role> optRole = repository.findByRoleName(userRole.value());
        return optRole.get();
    }

    @Override
    public Role getById(Long id) {
        Optional<Role> optRole = repository.getById(id);
        return optRole.orElse(null);
    }

    @Override
    public Response<List<RoleDTO>> getListRole() {
        List<RoleDTO> result = repository.findAllByDeletedAtIsNull().stream().map(role -> modelMapper.map(role,RoleDTO.class)).collect(Collectors.toList());
        return new Response<List<RoleDTO>>().withData(result);
    }

}
