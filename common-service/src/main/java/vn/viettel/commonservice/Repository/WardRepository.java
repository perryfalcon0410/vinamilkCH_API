package vn.viettel.commonservice.Repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.Ward;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface WardRepository extends BaseRepository<Ward> {
    @Query(value = "SELECT * FROM common_services.wards where district_id = :id", nativeQuery = true)
    List<Ward> getByDistrictId(long id);
}
