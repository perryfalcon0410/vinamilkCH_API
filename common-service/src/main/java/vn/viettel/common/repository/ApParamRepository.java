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

    @Query(value = "SELECT a FROM ApParam a WHERE a.apParamCode = :CODE ORDER BY a.status desc ")
    List<ApParam> findByCode(String CODE);

    @Query(value = "Select a FROM ApParam a Where a.type = 'SALEMT_PROMOTION_OBJECT' and a.status = 1 " )
    List<ApParam> getSalesChannel();

    @Query(value = "Select a FROM ApParam a Where a.type = 'SALEMT_PROMOTION_OBJECT' And a.apParamCode Like 'ONLINE%' and a.status = 1 ")
    List<ApParam> getOnlineOrderType();

    Optional<ApParam> findByTypeAndValueAndStatus(String type, String value, Integer status);

    @Query(value = "SELECT a FROM ApParam a WHERE a.apParamCode = :CODE AND a.type =:TYPE AND a.status = 1 ORDER BY a.status desc ")
    List<ApParam> findByCode(String CODE, String TYPE);

    @Query(value = "SELECT distinct a FROM ApParam a WHERE a.value in (:values) AND a.type = :type AND a.status = 1 ")
    List<ApParam> getApParams(List<String> values, String type);

}

