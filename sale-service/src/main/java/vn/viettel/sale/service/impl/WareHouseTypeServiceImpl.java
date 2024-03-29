package vn.viettel.sale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.WareHouseTypeService;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;
import vn.viettel.sale.service.feign.CustomerTypeClient;

import java.util.List;


@Service
public class WareHouseTypeServiceImpl extends BaseServiceImpl<WareHouseType, WareHouseTypeRepository> implements WareHouseTypeService {
    @Autowired
    CustomerTypeClient customerTypeClient;
    @Override
    public List<WareHouseTypeDTO> index(Long shopId) {
        Long wareHouseId = customerTypeClient.getWarehouseTypeByShopId(shopId);
        List<WareHouseTypeDTO> response = repository.findWithDefault(wareHouseId);
        return response;
    }
}
