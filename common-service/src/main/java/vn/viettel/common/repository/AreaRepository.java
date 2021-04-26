package vn.viettel.common.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.Area;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface AreaRepository extends BaseRepository<Area> {

    @Query(value = "SELECT * FROM AREAS WHERE TYPE = 2",nativeQuery = true)
    List<Area> getAllDistrict();
}
