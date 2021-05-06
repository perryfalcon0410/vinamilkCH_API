package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.SaleOrder;
import vn.viettel.sale.entities.StockCounting;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.repository.SaleOrderRepository;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.SaleService;
import vn.viettel.sale.service.WareHouseTypeService;
import vn.viettel.sale.service.dto.StockCountingDTO;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;
import vn.viettel.sale.specification.InventorySpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WareHouseTypeServiceImpl extends BaseServiceImpl<WareHouseType, WareHouseTypeRepository> implements WareHouseTypeService {
    @Override
    public Response<List<WareHouseTypeDTO>> index() {
        List<WareHouseType> wareHouseTypes = repository.findAll();
        List<WareHouseTypeDTO> response = wareHouseTypes.stream().map(e->modelMapper.map(e,WareHouseTypeDTO.class)).collect(Collectors.toList());
        return new Response< List<WareHouseTypeDTO>>().withData(response);
    }
}
