package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.City;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface CityRepository extends BaseRepository<City> {

    List<City> findAllByPrefectureId(long prefectureId);
}
