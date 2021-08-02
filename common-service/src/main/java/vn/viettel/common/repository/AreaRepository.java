package vn.viettel.common.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.Area;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends BaseRepository<Area> {

    @Query(value = "SELECT a FROM Area a WHERE a.type = 2")
    List<Area> getAllDistrict();

    @Query(value = "SELECT a FROM Area a " +
            "WHERE a.precinctName =:provinceName AND a.districtName =:districtName " +
            "AND a.precinctName =:precinctName AND a.type = 3 AND a.status = 1")
    Optional<Area> getArea(String provinceName, String districtName, String precinctName );

    @Query(value = "SELECT a.id FROM Area a " +
            " WHERE a.areaCode =:areaCode AND a.status = 1 ")
    Long getAreaIdByAreaCode(String areaCode);

    @Query("SELECT  NEW vn.viettel.core.dto.common.AreaDTO(a.id,a.areaCode,a.areaName,a.parentAreaId,a.province,a.provinceName," +
            " a.district,a.districtName,a.precinct,a.precinctName) FROM Area a " +
            " WHERE a.type =1")
    List<AreaDTO> getArea();

    @Query("SELECT  NEW vn.viettel.core.dto.common.AreaDTO(a.id,a.areaCode,a.areaName,a.parentAreaId,a.province,a.provinceName," +
            " a.district,a.districtName,a.precinct,a.precinctName) FROM Area a " +
            " WHERE a.type =2  AND a.parentAreaId =:Id")
    List<AreaDTO> getAreaByDistrictId(Long Id);

    @Query("SELECT  NEW vn.viettel.core.dto.common.AreaDTO(a.id,a.areaCode,a.areaName,a.parentAreaId,a.province,a.provinceName," +
            " a.district,a.districtName,a.precinct,a.precinctName) FROM Area a " +
            " WHERE a.type =3  AND a.parentAreaId =:Id")
    List<AreaDTO> getPrecinctsByDistrictId(Long Id);
}
