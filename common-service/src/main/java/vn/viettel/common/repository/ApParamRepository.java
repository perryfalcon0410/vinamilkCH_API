package vn.viettel.common.repository;

import vn.viettel.common.entities.ApParam;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;


public interface ApParamRepository extends BaseRepository<ApParam> {

    List<ApParam> findByTypeAndStatus(String type, Integer status);
}

