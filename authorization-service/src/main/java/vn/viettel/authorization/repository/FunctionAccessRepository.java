package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.authorization.FunctionAccess;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface FunctionAccessRepository extends BaseRepository<FunctionAccess> {
    List<FunctionAccess> findByPermissionId(Long permissionId);
}
