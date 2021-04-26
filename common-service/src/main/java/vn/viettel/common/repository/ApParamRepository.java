package vn.viettel.common.repository;

import vn.viettel.common.entities.ApParam;
import vn.viettel.common.entities.Area;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;


public interface ApParamRepository extends BaseRepository<ApParam> {
    ApParam getApParamByIdAndType(Long id, String type);
}

