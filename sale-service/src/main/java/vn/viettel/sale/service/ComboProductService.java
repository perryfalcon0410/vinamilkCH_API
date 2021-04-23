package vn.viettel.sale.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.ComboProductDTO;

import java.util.List;

public interface ComboProductService extends BaseService {

    Response<List<ComboProductDTO>> findComboProducts(String keyWord, Integer status);

    Response<ComboProductDTO> getComboProduct(Long id);
}
