package vn.viettel.authorization.repository;

import vn.viettel.authorization.entities.RoleUser;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface UserRoleRepository extends BaseRepository<RoleUser> {
    List<RoleUser> findByUserIdAndStatus(Long userId, Integer status);
}
