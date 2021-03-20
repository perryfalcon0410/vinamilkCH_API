package vn.viettel.common.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.common.repository.WardRepository;
import vn.viettel.common.service.WardService;
import vn.viettel.common.service.dto.WardDTO;
import vn.viettel.common.specification.WardSpecification;
import vn.viettel.core.db.entity.Ward;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WardServiceImpl extends BaseServiceImpl<Ward, WardRepository> implements WardService {
    @Override
    public Response<Page<WardDTO>> index(String searchKeywords, Pageable pageable) {
        Response<Page<WardDTO>> response = new Response<>();
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        Page<Ward> wards;

        wards = repository.findAll(Specification.where(WardSpecification.hasName(searchKeywords)), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<WardDTO> dtos = wards.map(this::mapWardToWardDTO);

        return response.withData(dtos);
    }

    @Override
    public Response<List<WardDTO>> getAllWardByDistrictIds(List<Long> provinceIds) {
        Response<List<WardDTO>> response = new Response<>();

        List<Ward> provinces = repository.findWardsByDistrictIdInAndDeletedAtIsNull(provinceIds);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<WardDTO> dtos = provinces.stream().map(ward -> modelMapper.map(ward, WardDTO.class)).collect(Collectors.toList());

        return response.withData(dtos);
    }

    private WardDTO mapWardToWardDTO(Ward Ward) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        WardDTO dto = modelMapper.map(Ward, WardDTO.class);
        return dto;
    }
}
