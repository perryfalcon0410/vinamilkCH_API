package vn.viettel.sale.service;

import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface CategoryDataService extends BaseService {
    Response<CategoryData> getCategoryDataById(Long id);
    Response<List<CategoryData>> getAllByCategoryGroupCode(String categoryGroupCode);
}
