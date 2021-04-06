package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.authorization.Form;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface FormRepository extends BaseRepository<Form> {
    @Query(value = "SELECT UNIQUE * FROM FORM f JOIN FUNCTION_ACCESS fa ON f.ID = fa.FORM_ID " +
            "WHERE fa.PERMISSION_ID = :permissionId", nativeQuery = true)
    List<Form> findByPermissionId(Long permissionId);
}
