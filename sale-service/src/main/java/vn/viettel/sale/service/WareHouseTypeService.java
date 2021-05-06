package vn.viettel.sale.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseService;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;

import java.util.Date;
import java.util.List;

public interface WareHouseTypeService extends BaseService {
    Response<List<WareHouseTypeDTO>> index(Long shopId);

}
