package vn.viettel.sale.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.repository.CategoryDataRepository;
import vn.viettel.sale.service.CategoryDataService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryDataServiceImpl extends BaseServiceImpl<CategoryData, CategoryDataRepository> implements CategoryDataService {
    @Override
    public Response<CategoryData> getCategoryDataById(Long id) {
        Optional<CategoryData> categoryData = repository.findById(id);
        if(!categoryData.isPresent())
        {
            throw new ValidateException(ResponseMessage.CATEGORY_DATA_NOT_EXISTS);
        }
        return new Response<CategoryData>().withData(categoryData.get());
    }

    @Override
    public Response<List<CategoryData>> getGenders() {
        List<CategoryData> genders = repository.findAll().stream()
                .filter(cd->cd.getCategoryGroupCode().equals("MASTER_SEX") && cd.getStatus()==1).collect(Collectors.toList());
        return new Response<List<CategoryData>>().withData(genders);
    }


}
