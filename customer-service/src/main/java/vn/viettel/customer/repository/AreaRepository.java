package vn.viettel.customer.repository;

import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface AreaRepository extends BaseRepository<Area> {
    List<Area> getAllByType(Integer type);
}
