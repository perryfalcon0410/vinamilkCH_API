package vn.viettel.sale.service;

import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;

import java.util.List;

public interface WareHouseTypeService extends BaseService {
    List<WareHouseTypeDTO> index(Long shopId);

}
