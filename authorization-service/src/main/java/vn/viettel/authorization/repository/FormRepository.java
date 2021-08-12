package vn.viettel.authorization.repository;

import vn.viettel.authorization.entities.Form;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface FormRepository extends BaseRepository<Form> {

    List<Form> findByStatusAndTypeNotNull(Integer status);

    List<Form> findByIdInAndStatus(List<Long> ids, Integer status);

}
