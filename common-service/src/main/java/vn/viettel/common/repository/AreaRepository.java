package vn.viettel.common.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.Area;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends BaseRepository<Area> {

    @Query(value = "SELECT * FROM AREAS WHERE TYPE = 2",nativeQuery = true)
    List<Area> getAllDistrict();

    @Query(value = "SELECT * FROM AREAS " +
            "WHERE PROVINCE_NAME =:provinceName AND DISTRICT_NAME =:districtName " +
            "AND PRECINCT_NAME =:precinctName AND TYPE = 3 AND STATUS = 1",nativeQuery = true)
    Optional<Area> getArea(String provinceName, String districtName, String precinctName );

    @Query(value = "SELECT id FROM AREAS " +
            "WHERE AREA_CODE =:areaCode AND STATUS = 1",nativeQuery = true)
    Long getAreaIdByAreaCode(String areaCode);
}
