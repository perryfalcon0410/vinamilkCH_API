package vn.viettel.common.repository;

import liquibase.util.StringUtil;
import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.ApParam;
import vn.viettel.common.entities.Area;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;


public interface ApParamRepository extends BaseRepository<ApParam> {

    List<ApParam> findByTypeAndStatus(String type, Integer status);
    ApParam getApParamByIdAndType(Long id, String type);
    @Query(value = "SELECT * FROM AP_PARAM WHERE AP_PARAM_CODE = :CODE", nativeQuery = true)
    Optional<ApParam> findByCode(String CODE);
}

