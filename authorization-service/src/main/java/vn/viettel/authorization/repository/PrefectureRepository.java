package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Prefecture;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface PrefectureRepository extends BaseRepository<Prefecture> {

    List<Prefecture> findAllByRegionId(long regionId);

}
