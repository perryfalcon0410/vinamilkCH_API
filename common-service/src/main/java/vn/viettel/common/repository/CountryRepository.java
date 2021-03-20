package vn.viettel.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import vn.viettel.core.db.entity.Country;
import vn.viettel.core.repository.BaseRepository;

public interface CountryRepository extends BaseRepository<Country>, JpaSpecificationExecutor<Country> {
    Page<Country> findAll(@Nullable Specification<Country> specification, Pageable pageable);
}
