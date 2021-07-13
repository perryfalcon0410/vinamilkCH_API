package vn.viettel.authorization.repository;

import vn.viettel.authorization.entities.Control;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ControlRepository extends BaseRepository<Control> {

    List<Control> findByIdInAndStatus(List<Long> formIds, Integer status);

    List<Control> findByStatusAndFormIdNotNull(Integer status);


}
