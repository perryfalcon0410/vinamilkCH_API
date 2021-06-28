package vn.viettel.authorization.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.authorization.entities.Form;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface FormRepository extends BaseRepository<Form> {
    @Query(value = "SELECT UNIQUE * FROM FORM f JOIN FUNCTION_ACCESS fa ON f.ID = fa.FORM_ID " +
            "WHERE fa.PERMISSION_ID = :permissionId", nativeQuery = true)
    List<Form> findByPermissionId(Long permissionId);

    @Query(value = "SELECT * FROM FORMS WHERE id = :id and status = :status", nativeQuery = true)
    Form findByIdAndStatus(Long id, Integer status);

    List<Form> findByTypeAndStatus(Integer type, Integer status);

    List<Form> findByStatus(Integer status);

    List<Form> findByIdInAndStatus(List<Long> ids, Integer status);

}
