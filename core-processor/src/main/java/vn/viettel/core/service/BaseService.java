package vn.viettel.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.service.dto.BaseDTO;

import java.util.List;

public interface BaseService {

    /**
     * Find item by id
     *
     * @param id:    the identification of item
     * @param clazz: class Type (use this for model mapping)
     * @return Type of DTO base item
     */
    <D extends BaseDTO> D findById(Long id, Class<D> clazz);

    /**
     * Find items b list of id
     *
     * @param ids:   the identification of items
     * @param clazz: Class Type (use this for model mapping)
     * @return
     */
    <D extends BaseDTO> List<D> findAllByIds(Iterable<Long> ids, Class<D> clazz);

    /**
     * Find all items without paging
     *
     * @param clazz: class Type (use this for model mapping)
     * @return
     */
    <D extends BaseDTO> List<D> findAll(Class<D> clazz);

    /**
     * Find all items in paging
     *
     * @param pageable
     * @return
     */
    <D extends BaseDTO> Page<BaseDTO> findAll(Pageable pageable, Class<D> clazz);

    /**
     * Save item into database
     *
     * @param item
     * @param clazz: class Type (use this for model mapping)
     * @return item
     */
    <D extends BaseDTO> D save(D item, Class<D> clazz);

    /**
     * Update item into database
     *
     * @param item
     * @param clazz: class Type (use this for model mapping)
     * @return item
     */
    <D extends BaseDTO> D update(D item, Class<D> clazz);

    /**
     * Delete item in database
     *
     * @param item
     * @param clazz: class Type (use this for model mapping)
     * @return
     */
    <D extends BaseDTO> D delete(D item, Class<D> clazz);

    /**
     * Item count
     *
     * @return Items count value
     */
    Long itemCount();

    /**
     * Check if item exists in database
     *
     * @param id
     * @return true: if exists, false: if not
     */
    boolean exists(Long id);

    /*
    Lock record and unlock record
     */
    <E extends BaseEntity> void lockUnLockRecord(List<E> entities, boolean isLock);

    <E extends BaseEntity> void lockUnLockRecord(E entity, boolean isLock);
}
