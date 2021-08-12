package vn.viettel.sale.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.dto.customer.CustomerTypeDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.sale.entities.WareHouseType;
import vn.viettel.sale.repository.WareHouseTypeRepository;
import vn.viettel.sale.service.WareHouseTypeService;
import vn.viettel.sale.service.dto.WareHouseTypeDTO;
import vn.viettel.sale.service.feign.CustomerTypeClient;

import java.util.ArrayList;
import java.util.List;


@Service
public class WareHouseTypeServiceImpl extends BaseServiceImpl<WareHouseType, WareHouseTypeRepository> implements WareHouseTypeService {
    @Autowired
    CustomerTypeClient customerTypeClient;
    @Override
    public List<WareHouseTypeDTO> index(Long shopId) {
        List<WareHouseType> wareHouseTypes = repository.findAll();
        Long wareHouseDTO = customerTypeClient.getWarehouseTypeByShopId(shopId);
        List<WareHouseTypeDTO> rs = new ArrayList<>();
        for(WareHouseType wh:wareHouseTypes ){
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            WareHouseTypeDTO dto = modelMapper.map(wh, WareHouseTypeDTO.class);
            if(wareHouseDTO.equals(wh.getId())){
                dto.setIsDefault(1);
            }
            rs.add(dto);
        }
        return rs;
    }
}
