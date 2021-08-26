package vn.viettel.common.service;

import vn.viettel.core.dto.common.CategoryDataDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface CategoryDataService extends BaseService {
    CategoryDataDTO getCategoryDataById(Long id);
    List<CategoryDataDTO> getGenders();
    List<CategoryDataDTO> getByCategoryGroupCode();
    CategoryDataDTO getReasonById(Long id);
    List<CategoryDataDTO> getListReasonExchange();

    List<CategoryDataDTO> getListReasonExchangeFeign(List<Long> ids, String sortName, String direction);
}
