package vn.viettel.authorization.repository;

import vn.viettel.authorization.entities.Control;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ControlRepository extends BaseRepository<Control> {
    List<Control> findByFormIdAndStatus(Long formId, Integer status);

    List<Control> findByFormIdInAndStatus(List<Long> formIds, Integer status);

    List<Control> findByStatus(Integer status);


}
