package vn.viettel.sale.service.impl;

import org.apache.regexp.RE;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.CategoryDataRepository;
import vn.viettel.sale.service.CategoryDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryDataServiceImpl extends BaseServiceImpl<CategoryData, CategoryDataRepository> implements CategoryDataService {
    @Override
    public Optional<CategoryData> getCategoryDataById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Response<List<CategoryData>> getAllByCategoryGroupCode(String categoryGroupCode) {
        List<CategoryData> categoryDataList = repository.getAllByCategoryGroupCode(categoryGroupCode);
        return new Response<List<CategoryData>>().withData(categoryDataList);
    }
}
