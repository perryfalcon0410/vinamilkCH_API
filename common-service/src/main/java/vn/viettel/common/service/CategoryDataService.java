package vn.viettel.common.service;

import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CategoryDataService extends BaseService {
    Response<CategoryDataDTO> getCategoryDataById(Long id);
    Response<List<CategoryDataDTO>> getGenders();
    List<CategoryDataDTO> getByCategoryGroupCode();
    CategoryDataDTO getReasonById(Long id);
    List<CategoryDataDTO> getListReasonExchange();
}
