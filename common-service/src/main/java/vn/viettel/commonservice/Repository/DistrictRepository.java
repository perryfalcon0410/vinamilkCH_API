package vn.viettel.commonservice.Repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.commonservice.Service.dto.ProDisDto;
import vn.viettel.core.db.entity.District;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface DistrictRepository extends BaseRepository<District> {
    @Query("SELECT new vn.viettel.commonservice.Service.dto.ProDisDto(p.id, p.name, d.id, d.name) FROM District d JOIN d.province p WHERE p.id = :proId")
    List<ProDisDto> getDistrictByProId(long proId);

    @Query(value = "SELECT * FROM DISTRICTS WHERE PROVINCE_ID = :id", nativeQuery = true)
    List<District> getDistrictByProvinceId(long id);
}
