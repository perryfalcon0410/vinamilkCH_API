package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.Permission;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigInteger;
import java.util.List;

public interface PermissionRepository extends BaseRepository<Permission> {

    @Query(value = "SELECT distinct function_id FROM permissions where role_id in :ids", nativeQuery = true)
    List<BigInteger> getFunctionIdByRoles(List<Integer> ids);

    @Query(value = "SELECT distinct action_id FROM user_services.permissions " +
            "where role_id in :roleIds and function_id = :funcId", nativeQuery = true)
    List<BigInteger> getActionsAllow(List<Integer> roleIds, int funcId);
}
