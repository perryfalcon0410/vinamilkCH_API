package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.CategoryDataRepository;
import vn.viettel.sale.service.CategoryDataService;

import java.util.Optional;

@Service
public class CategoryDataServiceImpl extends BaseServiceImpl<CategoryData, CategoryDataRepository> implements CategoryDataService {
    @Override
    public Optional<CategoryData> getCategoryDataById(Long id) {
        return repository.findById(id);
    }
}
