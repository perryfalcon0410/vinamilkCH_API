package vn.viettel.sale.repository;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface CategoryDataRepository extends BaseRepository<CategoryData> {
    List<CategoryData> getAllByCategoryGroupCode(String categoryGroupCode);
}
