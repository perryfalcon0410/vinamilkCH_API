package vn.viettel.common.service.impl;

import org.springframework.stereotype.Service;
import vn.viettel.common.repository.AreaRepository;
import vn.viettel.common.service.AreaService;
import vn.viettel.core.ResponseMessage;
import vn.viettel.common.entities.Area;
import vn.viettel.core.dto.common.AreaDTO;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AreaServiceImpl extends BaseServiceImpl<Area, AreaRepository> implements AreaService {

    @Override
    public Response<List<AreaDTO>> getAll() {
        List<Area> areas = repository.findAll();
        List<AreaDTO> areaDTOS = areas.stream()
                .map(area -> modelMapper.map(area,AreaDTO.class))
                .collect(Collectors.toList());

        return new Response<List<AreaDTO>>().withData(areaDTOS);
    }

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
    public Response<List<AreaDTO>> getPrecinctsByProvinceId(Long provinceId) {
        List<AreaDTO> precincts = repository.getPrecinctsByProvinceId(provinceId)
                .stream().map(area -> modelMapper.map(area,AreaDTO.class))
                .collect(Collectors.toList());

        return new Response<List<AreaDTO>>().withData(precincts);
    }
}
