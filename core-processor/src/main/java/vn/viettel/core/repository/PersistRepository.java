package vn.viettel.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistRepository<T/* extends BaseEntity*/> extends JpaRepository<T, Long> {

}
