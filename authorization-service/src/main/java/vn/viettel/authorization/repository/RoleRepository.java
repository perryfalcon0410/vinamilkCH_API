package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.Role;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface RoleRepository extends BaseRepository<Role> {

    List<Role> findByIdInAndStatus(List<Long> ids, Integer status);

    @Query("Select distinct r from User u " +
            "join RoleUser ru on u.id = ru.userId " +
            "join Role r on r.id = ru.roleId " +
            "where u.id =:useId and ru.status =1 and r.status = 1")
    List<Role> findRoles(Long useId);
}
