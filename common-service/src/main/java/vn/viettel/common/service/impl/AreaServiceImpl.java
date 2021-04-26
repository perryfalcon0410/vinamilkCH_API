package vn.viettel.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.common.messaging.AreaSearch;
import vn.viettel.common.repository.AreaRepository;
import vn.viettel.common.service.AreaService;
import vn.viettel.common.service.feign.ShopClient;
import vn.viettel.core.ResponseMessage;
import vn.viettel.common.entities.Area;
import vn.viettel.core.dto.ShopDTO;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AreaServiceImpl extends BaseServiceImpl<Area, AreaRepository> implements AreaService {

    @Autowired
    ShopClient shopClient;

    @Override
    public Response<AreaDTO> getAreaById(Long id) {
        Optional<Area> area = repository.findById(id);
        if(!area.isPresent())
        {
            throw new ValidateException(ResponseMessage.AREA_NOT_EXISTS);
        }

        return new Response<AreaDTO>().withData(modelMapper.map(area.get(),AreaDTO.class));
    }

    @Override
    public Response<List<AreaDTO>> getProvinces() {
        List<Area> areas = repository.findAll();
        List<AreaDTO> areaDTOS = areas.stream()
                .filter(a->a.getType() == 1)
                .map(area -> modelMapper.map(area,AreaDTO.class))
                .collect(Collectors.toList());

        return new Response<List<AreaDTO>>().withData(areaDTOS);
    }

    @Override
    public Response<List<AreaDTO>> getDistrictsByProvinceId(Long provinceId) {
        List<Area> areas = repository.findAll();
        List<AreaDTO> districts = areas.stream()
                .filter(a->a.getType() == 2 && a.getParentAreaId() == provinceId)
                .map(area -> modelMapper.map(area,AreaDTO.class))
                .collect(Collectors.toList());

        return new Response<List<AreaDTO>>().withData(districts);
    }

    @Override
    public Response<List<AreaDTO>> getPrecinctsByDistrictId(Long districtId) {
        List<Area> areas = repository.findAll();
        List<AreaDTO> precincts = areas.stream()
                .filter(a->a.getType() == 3 && a.getParentAreaId() == districtId)
                .map(area -> modelMapper.map(area,AreaDTO.class))
                .collect(Collectors.toList());

        return new Response<List<AreaDTO>>().withData(precincts);
    }

    @Override
    public Response<List<AreaSearch>> getDistrictsToSearchCustomer(Long shopId) {
        List<Area> areas = repository.getAllDistrict();
        ShopDTO shopDTO = shopClient.getShopById(shopId).getData();
        Area precinct = repository.findById(shopDTO.getAreaId()).orElse(null);
        Long districtId = null;
        if(precinct != null)
        {
            districtId = precinct.getParentAreaId();
        }
        Long finalDistrictId = districtId;
        List<AreaSearch> areaSearches = areas.stream().map(area -> this.mapAreaToAreaSearch(area, finalDistrictId)).collect(Collectors.toList());

        return new Response<List<AreaSearch>>().withData(areaSearches);
    }

    private AreaSearch mapAreaToAreaSearch(Area area, Long districtId)
    {
        String provinceAndDistrict = area.getProvinceName()+" - "+area.getDistrictName();
        AreaSearch areaSearch = modelMapper.map(area,AreaSearch.class);
        areaSearch.setProvinceAndDistrictName(provinceAndDistrict);
        areaSearch.setDefault((areaSearch.getId() == districtId) ? true : false);
        return areaSearch;
    }
}
