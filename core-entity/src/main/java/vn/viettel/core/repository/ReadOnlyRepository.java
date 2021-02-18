package vn.viettel.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.viettel.core.db.entity.BaseEntity;

public interface ReadOnlyRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    // get all not paginate
    List<T> findByDeletedAtIsNull();

    // get all paginate
    Page<T> findByDeletedAtIsNull(Pageable pageable);

    // find by id end deleted at is null
    T findByIdAndDeletedAtIsNull(Long id);

}
