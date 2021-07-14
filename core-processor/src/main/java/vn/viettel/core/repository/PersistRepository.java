package vn.viettel.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.viettel.core.db.entity.BaseEntity;

public interface PersistRepository<T/* extends BaseEntity*/> extends JpaRepository<T, Long> {

}
