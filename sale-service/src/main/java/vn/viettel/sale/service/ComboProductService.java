package vn.viettel.sale.service;

import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.ComboProductDTO;

import java.util.List;

public interface ComboProductService extends BaseService {

  List<ComboProductDTO>findComboProducts(String keyWord, Integer status);

  ComboProductDTO getComboProduct(Long id);
}
