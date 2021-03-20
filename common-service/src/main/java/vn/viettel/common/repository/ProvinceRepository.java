package vn.viettel.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import vn.viettel.core.db.entity.Province;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ProvinceRepository extends BaseRepository<Province>, JpaSpecificationExecutor<Province> {
    Page<Province> findAll(@Nullable Specification<Province> specification, Pageable pageable);

    List<Province> findProvincesByAreaIdInAndDeletedAtIsNull(List<Long> areaIds);

}
