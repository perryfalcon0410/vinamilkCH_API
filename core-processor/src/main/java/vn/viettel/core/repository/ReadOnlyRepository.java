package vn.viettel.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadOnlyRepository<T/* extends BaseEntity*/> extends JpaRepository<T, Long> {

    // get all not paginate
    List<T> findAll();

    // get all paginate
    Page<T> findAll(Pageable pageable);

    // find by id end deleted at is null
    T getById(Long id);

}
