package vn.viettel.core.repository;

public interface BaseRepository<T/* extends BaseEntity*/> extends ReadOnlyRepository<T>, PersistRepository<T> {

	// check exist by id
	boolean existsById(Long id);

}
