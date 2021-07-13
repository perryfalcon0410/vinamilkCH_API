package vn.viettel.core.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.exception.ApplicationException;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.service.dto.ControlDTO;
import vn.viettel.core.service.dto.PermissionDTO;
import vn.viettel.core.service.helper.InstanceInitializerHelper;
import vn.viettel.core.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseServiceImpl<E extends BaseEntity, R extends BaseRepository<E>> implements BaseService {

    @Autowired
    protected R repository;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    private SecurityContexHolder securityContexHolder;

    @PersistenceContext
    public EntityManager entityManager;

    protected Class<E> clazz = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseRepository.class);

    @Override
    public <D extends BaseDTO> D findById(Long id, Class<D> clazz) {
        E dbItem = repository.findById(id).orElse(null);
        return dbItem == null ? null : modelMapper.map(dbItem, clazz);
    }

    @Override
    public <D extends BaseDTO> List<D> findAll(Class<D> clazz) {
        List<E> dbItems = repository.findAll();
        List<D> retItems = new ArrayList<D>();
        dbItems.forEach(item -> {
            D nIntance = InstanceInitializerHelper.newDTOIntance(clazz);
            modelMapper.map(item, nIntance);
            retItems.add(nIntance);
        });
        return retItems;
    }

    @Override
    public <D extends BaseDTO> Page<BaseDTO> findAll(Pageable pageable, Class<D> clazz) {
        List<D> retItems = new ArrayList<D>();
        Page<E> dbItems = repository.findAll(pageable);
        dbItems.forEach(item -> {
            D nIntance = InstanceInitializerHelper.newDTOIntance(clazz);
            modelMapper.map(item, nIntance);
            retItems.add(nIntance);
        });
        // TODO: need to implement on paging
        return null;
    }

    @Override
    public <D extends BaseDTO> List<D> findAllByIds(Iterable<Long> ids, Class<D> clazz) {
        List<E> dbItems = repository.findAllById(ids);
        List<D> retItems = new ArrayList<D>();
        dbItems.forEach(item -> {
            D nIntance = InstanceInitializerHelper.newDTOIntance(clazz);
            modelMapper.map(item, nIntance);
            retItems.add(nIntance);
        });
        return retItems;
    }

    @Override
    public Long itemCount() {
        return repository.count();
    }

    @Override
    public <D extends BaseDTO> D save(D item, Class<D> clazz) {
        E entity = modelMapper.map(item, this.processingEntityClazz());
        E dbItem = repository.save(entity);
        D nIntance = InstanceInitializerHelper.newDTOIntance(clazz);
        modelMapper.map(dbItem, nIntance);
        return nIntance;
    }

    @Override
    public <D extends BaseDTO> D update(D item, Class<D> clazz) {
        return this.save(item, clazz);
    }

    @Override
    public <D extends BaseDTO> D delete(D item, Class<D> clazz)  throws ApplicationException {
        if(item == null || item.getId() == null || item.getId() == 0)
            throw new ApplicationException("Can not delete null value");

        try{
            repository.deleteById(item.getId());
        }catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }

        return null;
    }

    @Override
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    @SuppressWarnings("unchecked")
    protected Class<E> processingEntityClazz() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<E>) paramType.getActualTypeArguments()[0];
    }

    /*public boolean checkUserPermission(List<PermissionDTO> permissionList, Long formId, Long controlId) {
        boolean havePrivilege = false;

        for (PermissionDTO permission : permissionList) {
            List<ControlDTO> controlList = permission.getControls();
            if (permission.getId() == formId && controlList.stream().anyMatch(ctrl -> ctrl.getId().equals(controlId)))
                havePrivilege = true;
        }
        return havePrivilege;
    }*/

//    public List<E> lockUnLockRecord(List<E> entities, boolean isLock){
//        if(entities == null || entities.isEmpty()) return;
//        for (Object entity : entities.toArray()){
////            if(clazz != null && clazz.isAssignableFrom(entity.getClass())) {
//                if (isLock) entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
//                else entityManager.lock(entity, LockModeType.NONE);
//            }
////        }
//    }

    @Override
    public <E extends BaseEntity> void lockUnLockRecord(List<E> entities, boolean isLock) {
        if(entities == null || entities.isEmpty()) return ;
        for (Object entity : entities.toArray()){
//            if(clazz != null && clazz.isAssignableFrom(entity.getClass())) {
            if (isLock) entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
            else entityManager.lock(entity, LockModeType.NONE);
        }
//        }
    }

    @Override
    public <E extends BaseEntity> void lockUnLockRecord(E entity, boolean isLock) {

    }

//    public void lockUnLockRecord(Object entity, boolean isLock){
//        if(entity == null) return;
////        if(clazz != null && clazz.isAssignableFrom(entity.getClass())) {
//            if (isLock) entityManager.lock(entity, LockModeType.PESSIMISTIC_WRITE);
//            else entityManager.lock(entity, LockModeType.NONE);
////        }
//    }
}
