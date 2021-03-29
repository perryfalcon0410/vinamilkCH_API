package vn.viettel.sale.service;

import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.service.BaseService;

import java.util.Optional;

public interface CategoryDataService extends BaseService {
    Optional<CategoryData> getCategoryDataById(Long id);
}
