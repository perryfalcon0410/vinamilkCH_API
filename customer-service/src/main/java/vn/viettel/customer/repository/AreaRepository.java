package vn.viettel.customer.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.core.db.entity.common.Area;
import vn.viettel.core.db.entity.common.Customer;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends BaseRepository<Area> {
    Optional<Area> getByIdAndType(Long id, Integer type);

    @Query(value = "SELECT * FROM AREAS WHERE PARENT_AREA_ID IN (SELECT ID FROM AREAS WHERE PARENT_AREA_ID =:provinceId)", nativeQuery = true)
    List<Area> getPrecinctsByProvinceId(Long provinceId);
}
