package vn.viettel.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import vn.viettel.core.db.entity.District;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;


public interface DistrictRepository extends BaseRepository<District>, JpaSpecificationExecutor<District> {
    Page<District> findAll(@Nullable Specification<District> specification, Pageable pageable);

    List<District> findDistrictsByProvinceIdInAndDeletedAtIsNull(List<Long> provinceIds);

    List<District> findDistrictsByIdInAndDeletedAtIsNull(List<Long> ids);
}
