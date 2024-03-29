package vn.viettel.common.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.viettel.common.entities.CategoryData;
import vn.viettel.common.repository.CategoryDataRepository;
import vn.viettel.common.service.CategoryDataService;
import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryDataServiceImpl extends BaseServiceImpl<CategoryData, CategoryDataRepository> implements CategoryDataService {
    @Override
    public CategoryDataDTO getCategoryDataById(Long id) {
        Optional<CategoryData> categoryData = repository.findById(id);
        if(!categoryData.isPresent())
            throw new ValidateException(ResponseMessage.CATEGORY_DATA_NOT_EXISTS);
        return modelMapper.map(categoryData.get(), CategoryDataDTO.class);
    }

    @Override
    public List<CategoryDataDTO> getGenders() {
        List<CategoryData> genders = repository.findAll().stream()
                .filter(cd->cd.getCategoryGroupCode().equals("MASTER_SEX")&&cd.getStatus()==1).collect(Collectors.toList());
        return genders.stream().map(
                item -> modelMapper.map(item, CategoryDataDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<CategoryDataDTO> getByCategoryGroupCode() {
        try {
            return repository.findByCategoryGroupCode().stream().map(item -> modelMapper.map(item, CategoryDataDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CategoryDataDTO getReasonById(Long id) {
        return modelMapper.map(repository.getReasonById(id), CategoryDataDTO.class);
    }

    @Override
    public List<CategoryDataDTO> getListReasonExchangeFeign(List<Long> ids, String sortName, String direction) {
        Pageable pageable = PageRequest.of(0, 1000);
        if(sortName!=null && !sortName.isEmpty()) {
            if(direction == null) direction = "ASC";
            Sort sort = Sort.by(Sort.Direction.valueOf(direction.trim()), sortName.trim());
            pageable = PageRequest.of(0, 1000, sort);
        }
        return repository.listReasonExchangeTransId(ids, pageable).getContent();
    }

    @Override
    public List<CategoryDataDTO> getListReasonExchange() {
        return repository.listReasonExchangeTrans();
    }
}
