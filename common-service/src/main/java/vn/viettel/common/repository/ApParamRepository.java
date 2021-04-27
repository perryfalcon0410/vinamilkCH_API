package vn.viettel.common.repository;

import vn.viettel.common.entities.ApParam;
import vn.viettel.common.entities.Area;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;
import java.util.Optional;


public interface ApParamRepository extends BaseRepository<ApParam> {

    List<ApParam> findByTypeAndStatus(String type, Integer status);
    ApParam getApParamByIdAndType(Long id, String type);
    List<ApParam> getApParamByType( String type);
}

