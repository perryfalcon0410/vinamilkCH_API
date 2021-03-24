package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.authorization.RoleUser;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface UserRoleRepository extends BaseRepository<RoleUser> {
    List<RoleUser> findByUserId(Long userId);
}
