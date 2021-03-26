package vn.viettel.customer.repository;

import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.repository.BaseRepository;

import java.util.Optional;

public interface CategoryDataRepository extends BaseRepository<CategoryData> {
    CategoryData findById(Integer id);
}
