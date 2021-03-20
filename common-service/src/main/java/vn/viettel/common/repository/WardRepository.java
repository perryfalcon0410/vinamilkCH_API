package vn.viettel.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import vn.viettel.core.db.entity.Ward;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;


public interface WardRepository extends BaseRepository<Ward> {
    Page<Ward> findAll(@Nullable Specification<Ward> specification, Pageable pageable);

    List<Ward> findWardsByDistrictIdInAndDeletedAtIsNull(List<Long> districtIds);

}
