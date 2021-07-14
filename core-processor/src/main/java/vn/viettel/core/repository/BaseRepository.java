package vn.viettel.core.repository;

import vn.viettel.core.db.entity.BaseEntity;

public interface BaseRepository<T/* extends BaseEntity*/> extends ReadOnlyRepository<T>, PersistRepository<T> {

	// check exist by id
	boolean existsById(Long id);

}
