package vn.viettel.common.repository;

import org.springframework.data.jpa.repository.Query;
import vn.viettel.common.entities.ApParam;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;


public interface ApParamRepository extends BaseRepository<ApParam> {

    List<ApParam> findByTypeAndStatus(String type, Integer status);
    ApParam getApParamByIdAndType(Long id, String type);
    List<ApParam> getApParamByType( String type);
    List<ApParam> getApParamByTypeAndStatus( String type,Integer status);
    @Query(value = "SELECT a FROM ApParam a WHERE a.apParamCode = :CODE AND a.status = 1")
    Optional<ApParam> findByCode(String CODE);
    @Query(value = "SELECT * FROM AP_PARAM WHERE TYPE = 'SALEMT_PROMOTION_OBJECT' and STATUS = 1" , nativeQuery = true)
    List<ApParam> getSalesChannel();

    Optional<ApParam> findByTypeAndValueAndStatus(String type, String value, Integer status);
}

