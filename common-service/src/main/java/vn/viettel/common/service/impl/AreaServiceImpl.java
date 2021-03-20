package vn.viettel.common.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.common.repository.AreaRepository;
import vn.viettel.common.service.AreaService;
import vn.viettel.common.service.dto.AreaDTO;
import vn.viettel.common.specification.AreaSpecification;
import vn.viettel.core.db.entity.Area;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AreaServiceImpl extends BaseServiceImpl<Area, AreaRepository> implements AreaService {
    @Override
    public Response<Page<AreaDTO>> index(String searchKeywords, Pageable pageable) {
        Response<Page<AreaDTO>> response = new Response<>();
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        Page<Area> areas;

        areas = repository.findAll(Specification.where(AreaSpecification.hasName(searchKeywords)), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<AreaDTO> dtos = areas.map(this::mapAreaToAreaDTO);

        return response.withData(dtos);
    }

    @Override
    public Response<List<AreaDTO>> getAllAreaByCountryIds(List<Long> countryIds) {
        Response<List<AreaDTO>> response = new Response<>();

        List<Area> areas = repository.findAreasByCountryIdInAndDeletedAtIsNull(countryIds);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<AreaDTO> dtos = areas.stream().map(area -> modelMapper.map(area, AreaDTO.class)).collect(Collectors.toList());

        return response.withData(dtos);
    }

    private AreaDTO mapAreaToAreaDTO(Area area) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        AreaDTO dto = modelMapper.map(area, AreaDTO.class);
        return dto;
    }
}
