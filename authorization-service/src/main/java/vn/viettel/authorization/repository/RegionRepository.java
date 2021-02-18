package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Region;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface RegionRepository extends BaseRepository<Region> {

    List<Region> findAllByCountryId(long countryId);
}
