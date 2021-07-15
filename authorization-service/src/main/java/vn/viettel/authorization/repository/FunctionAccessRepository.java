package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.FunctionAccess;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Set;

public interface FunctionAccessRepository extends BaseRepository<FunctionAccess> {

    // Form module ko có control, form nhỏ nhất ms có control mà FunctionAccess lưu đủ 3 type
    @Query("Select DISTINCT f From FunctionAccess f " +
                "Join Form fo On f.formId = fo.id " +
                "Left Join Control c On f.controlId = c.id " +
                "Join Permission p On p.id = f.permissionId " +
            "Where f.permissionId In :permisionIds " +
            "And f.status = 1 And fo.status = 1 And p.status = 1 " +
            "And (" +
            "(f.controlId is null And c.status is null ) " +
            "OR " +
            "(f.controlId is not null And c.status = 1 ) " +
            ") ")
    List<FunctionAccess> findByPermissionIds(Set<Long> permisionIds);
}
