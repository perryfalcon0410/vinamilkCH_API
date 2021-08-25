package vn.viettel.common.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.common.entities.Area;
import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.repository.AreaRepository;
import vn.viettel.common.service.AreaService;
import vn.viettel.common.service.dto.AreaDefaultDTO;
import vn.viettel.common.service.feign.ShopClient;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.util.ResponseMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AreaServiceImpl extends BaseServiceImpl<Area, AreaRepository> implements AreaService {

    @Autowired
    ShopClient shopClient;

    @Override
    public AreaDTO getAreaById(Long id) {
        Optional<Area> area = repository.findById(id);
        if(!area.isPresent())
        {
            throw new ValidateException(ResponseMessage.AREA_NOT_EXISTS);
        }

        return modelMapper.map(area.get(),AreaDTO.class);
    }

    @Override
    public List<AreaDTO> getProvinces() {
        List<AreaDTO> areaDTOS = repository.getArea();
        return areaDTOS;
    }
    @Override
    public List<AreaDTO> getDistrictsByProvinceId(Long provinceId) {
        List<AreaDTO> districts = repository.getAreaByDistrictId(provinceId);
        return districts;
    }
    @Override
    public List<AreaDTO> getPrecinctsByDistrictId(Long districtId) {
        List<AreaDTO> precincts = repository.getPrecinctsByDistrictId(districtId);
        return precincts;
    }
    @Override
    public List<AreaSearch> getDistrictsToSearchCustomer(Long shopId) {

        ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();
        Long districtId = null;
       if(shopDTO.getAreaId() != null) {
            Area precinct = repository.findById(shopDTO.getAreaId()).orElse(null);
             if(precinct!=null) districtId = precinct.getParentAreaId();
        }

        List<AreaSearch> areas = repository.getAllDistrict(districtId);
        return areas;
    }

    @Override
    public AreaDTO getArea(String provinceName, String districtName, String precinctName) {
        Area area = repository.getArea(provinceName, districtName, precinctName).orElse(null);
        if(area == null) return null;
//            .orElseThrow(() -> new ValidateException(ResponseMessage.AREA_NOT_EXISTS));
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        AreaDTO areaDTO = modelMapper.map(area, AreaDTO.class);
        return areaDTO;
    }

    @Override
    public AreaDefaultDTO getAreaDefault(Long shopId) {
        AreaDefaultDTO areaDefaultDTO = new AreaDefaultDTO();
        if(shopId != null)
        {
            ShopDTO shopDTO = shopClient.getShopByIdV1(shopId).getData();
            if(shopDTO != null)
            {
                if(shopDTO.getAreaId() != null)
                {
                    Area area = repository.getById(shopDTO.getAreaId());
                    if(area != null)
                    {
                        areaDefaultDTO.setProvinceId(repository.getAreaIdByAreaCode(area.getProvince()));
                        areaDefaultDTO.setDistrictId(area.getParentAreaId());
                        areaDefaultDTO.setPrecinctId(area.getId());
                    }
                }

            }
        }
        return areaDefaultDTO;
    }

}
