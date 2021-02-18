package vn.viettel.core.service;

import vn.viettel.core.annotation.Scalar;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.db.entity.status.Object;
import vn.viettel.core.dto.search.CustomPage;
import vn.viettel.core.dto.search.Param;
import vn.viettel.core.dto.search.SearchParameter;
import vn.viettel.core.repository.BaseRepository;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.service.dto.BaseDTO;
import vn.viettel.core.service.helper.InstanceInitializerHelper;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BaseServiceImpl<E extends BaseEntity, R extends BaseRepository<E>> implements BaseService {

    @Autowired
    protected R repository;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    private SecurityContexHolder securityContexHolder;

    @PersistenceContext
    EntityManager entityManager;

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
    public <D extends BaseDTO> D delete(D item, Class<D> clazz) {
        item.setDeletedAt(LocalDateTime.now());
        return this.save(item, clazz);
    }

    @Override
    public boolean exists(Long id) {
        return repository.existsByIdAndDeletedAtIsNull(id);
    }

    @SuppressWarnings("unchecked")
    protected Class<E> processingEntityClazz() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<E>) paramType.getActualTypeArguments()[0];
    }

    public String getRole() {
        return securityContexHolder.getContext().getRole();
    }

    public Long getUserId() {
        return securityContexHolder.getContext().getUserId();
    }

    public Object getObject() {
        return securityContexHolder.getContext().getObject();
    }

    public Long getObjectId() {
        return securityContexHolder.getContext().getObjectId();
    }

    public boolean isAdministrator() {
        return UserRole.ADMIN.value().equals(getRole());
    }

    public boolean isDistributor() {
        return UserRole.DISTRIBUTOR.value().equals(getRole());
    }

    public boolean isShopOwner() {
        return UserRole.SHOP_OWNER.value().equals(getRole());
    }

    public boolean isShopManager() {
        return UserRole.SHOP_MANAGER.value().equals(getRole());
    }

    public boolean isShopEmployee() {
        return UserRole.SHOP_EMPLOYEE.value().equals(getRole());
    }

    public boolean isGroupManager() {
        return UserRole.GROUP_MANAGER.value().equals(getRole());
    }

    public boolean isGroupEmployee() {
        return UserRole.GROUP_EMPLOYEE.value().equals(getRole());
    }

    private SearchParameter prepareSearchParameter(List<String> searchColumn, Pageable pageable, Param... args) {
        // 1. Forming search column
        String paramValueSearchColumn = null;
        if (!CollectionUtils.isEmpty(searchColumn)) {
            paramValueSearchColumn = String.join(",", searchColumn);
        }
        // 2. Forming offset, limit
        long paramValueOffset = pageable.getOffset();
        long paramValueLimit = pageable.getPageSize();
        // 3. Forming sorting
        StringBuilder paramValueSort = new StringBuilder();
        pageable.getSort().forEach(sortItem -> paramValueSort.append(sortItem.getProperty()).append(" ")
                .append(sortItem.getDirection().toString())
                .append(","));
        if (paramValueSort.length() > 0) {
            paramValueSort.setLength(paramValueSort.length() - 1);
        }
        // 4. Forming other arguments
        List<Param> nameParameters = new ArrayList<>();
        if (args != null && args.length > 0) {
            nameParameters = Arrays.stream(args).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return new SearchParameter(paramValueSearchColumn, paramValueOffset,
                paramValueLimit, paramValueSort, nameParameters);
    }

    @Override
    public <T> CustomPage<T> getProcedureQuery(String query, Class tClass, List<String> searchColumn, Pageable pageable, Param... args) {
        // 1. Prepare search procedure
        StringBuilder strQuery = new StringBuilder("call ");
        strQuery.append(query).append("(:searchColumn, :offset, :limit, :sort");

        // 2. append other arguments
        SearchParameter searchParameter = this.prepareSearchParameter(searchColumn, pageable, args);
        if (!CollectionUtils.isEmpty(searchParameter.getNameParameters())) {
            searchParameter.getNameParameters().forEach(item -> {
                String strParam = ", :" + item.getName();
                strQuery.append(strParam);
            });
        }
        strQuery.append(")");

        // 3. Calling (get data) procedure
        Query sqlQuery = entityManager.createNativeQuery(strQuery.toString());
        sqlQuery.setParameter("searchColumn", searchParameter.getParamValueSearchColumn());
        sqlQuery.setParameter("offset", searchParameter.getParamValueOffset());
        sqlQuery.setParameter("limit", searchParameter.getParamValueLimit());
        sqlQuery.setParameter("sort", searchParameter.getParamValueSort().toString());
        if (!CollectionUtils.isEmpty(searchParameter.getNameParameters())) {
            searchParameter.getNameParameters().forEach(item -> sqlQuery.setParameter(item.getName(), item.getValue()));
        }
        // transform result
        List result = this.transformAndQuery(sqlQuery, tClass);

        // 5. Return the basic Paging
        return new CustomPage<T>(result, pageable);
    }

    @Override
    public <T> CustomPage<T> getProcedurePagination(String query, String countQuery,
                                                    Class tClass,
                                                    List<String> searchColumn,
                                                    Pageable pageable, Param... args) {
        // 1. Call the search procedure query
        CustomPage<T> subPageObj = this.getProcedureQuery(query, tClass, searchColumn, pageable, args);

        // 2. Prepare count query
        StringBuilder strCountQuery = new StringBuilder("call ");
        strCountQuery.append(countQuery).append("(:searchColumn");

        // 3. prepare search parameter
        SearchParameter searchParameter = this.prepareSearchParameter(searchColumn, pageable, args);
        if (!CollectionUtils.isEmpty(searchParameter.getNameParameters())) {
            searchParameter.getNameParameters().forEach(item -> {
                String strParam = ", :" + item.getName();
                strCountQuery.append(strParam);
            });
        }
        strCountQuery.append(")");

        // 4. Calling (get data) procedure
        Query sqlCountQuery = entityManager.createNativeQuery(strCountQuery.toString());
        sqlCountQuery.setParameter("searchColumn", searchParameter.getParamValueSearchColumn());
        if (!CollectionUtils.isEmpty(searchParameter.getNameParameters())) {
            searchParameter.getNameParameters().forEach(item -> sqlCountQuery.setParameter(item.getName(), item.getValue()));
        }
        java.lang.Object totalRecords = sqlCountQuery.getSingleResult();

        // 5. Transform into page object
        return new CustomPage<>(
                subPageObj.getContent(), subPageObj.getPageable(), Long.parseLong(totalRecords.toString())
        );
    }

    @SuppressWarnings({"deprecation", "rawtypes"})
    private List transformAndQuery(Query sqlQuery, Class tClass) {
        NativeQuery unwrapQuery = sqlQuery.unwrap(NativeQuery.class);

        // now check scalar
        for (Field field : tClass.getDeclaredFields()) {
            Arrays.stream(field.getDeclaredAnnotations()).filter(item ->
                    item.annotationType().getSimpleName().toLowerCase().contentEquals(
                            Scalar.class.getSimpleName().toLowerCase()
                    )
            ).findAny().ifPresent(item -> {
                Scalar annotate = field.getAnnotation(Scalar.class);
                unwrapQuery.addScalar(field.getName(), annotate.type().getType());
            });
        }
        sqlQuery = unwrapQuery.setResultTransformer(Transformers.aliasToBean(tClass));

        return sqlQuery.getResultList();
    }

}
