package vn.viettel.customer.repository;

import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends BaseRepository<Area> {
    Optional<Area> getByIdAndType(Long id, Integer type);
}
