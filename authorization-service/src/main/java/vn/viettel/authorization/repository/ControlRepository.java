package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.authorization.Control;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ControlRepository extends BaseRepository<Control> {
    List<Control> findByFormId(Long formId);
}
