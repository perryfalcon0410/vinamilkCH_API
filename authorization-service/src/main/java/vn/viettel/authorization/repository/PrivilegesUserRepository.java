package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.PrivilegesUsers;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrivilegesUserRepository extends BaseRepository<PrivilegesUsers> {

    @Query(value = "SELECT * FROM privileges_users "
            + "WHERE management_user_id = :id "
            + "AND deleted_at IS NULL", nativeQuery = true)
    List<PrivilegesUsers> getPrivilegesByManagementUserId(@Param("id") Long id);


    @Modifying
    @Query(value = "DELETE FROM privileges_users "
            + "WHERE management_user_id = :id "
            + "AND deleted_at IS NULL", nativeQuery = true)
    void deleteByUserId(@Param("id") Long id);

    @Query(value = "SELECT * FROM privileges_users "
            + "WHERE management_user_id = :idUser AND management_privilege_id = :idPrivilege "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<PrivilegesUsers> getByPrivilegeIdAndUserId(@Param("idPrivilege") Long idPrivilege, @Param("idUser") Long idUser);

}
