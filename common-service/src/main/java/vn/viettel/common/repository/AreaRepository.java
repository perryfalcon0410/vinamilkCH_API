package vn.viettel.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import vn.viettel.core.db.entity.Area;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface AreaRepository extends BaseRepository<Area>, JpaSpecificationExecutor<Area> {
    Page<Area> findAll(@Nullable Specification<Area> specification, Pageable pageable);

    List<Area> findAreasByCountryIdInAndDeletedAtIsNull(List<Long> countryIds);
}
