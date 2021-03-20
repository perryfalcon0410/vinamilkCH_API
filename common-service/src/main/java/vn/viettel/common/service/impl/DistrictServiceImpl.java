package vn.viettel.common.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.common.repository.DistrictRepository;
import vn.viettel.common.service.DistrictService;
import vn.viettel.common.service.dto.DistrictDTO;
import vn.viettel.common.service.dto.ProvinceDTO;
import vn.viettel.common.specification.DistrictSpecification;
import vn.viettel.core.db.entity.District;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistrictServiceImpl extends BaseServiceImpl<District, DistrictRepository> implements DistrictService {
    @Override
    public Response<Page<DistrictDTO>> index(String searchKeywords, Pageable pageable) {
        Response<Page<DistrictDTO>> response = new Response<>();
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        Page<District> districts;

        districts = repository.findAll(Specification.where(DistrictSpecification.hasName(searchKeywords)), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<DistrictDTO> dtos = districts.map(this::mapDistrictToDistrictDTO);

        return response.withData(dtos);
    }

    @Override
    public Response<List<ProvinceDTO>> getAllDistrictByAreaIds(List<Long> provinceIds) {
        Response<List<ProvinceDTO>> response = new Response<>();

        List<District> districts = repository.findDistrictsByProvinceIdInAndDeletedAtIsNull(provinceIds);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<ProvinceDTO> dtos = districts.stream().map(district -> modelMapper.map(district, ProvinceDTO.class)).collect(Collectors.toList());

        return response.withData(dtos);
    }

    private DistrictDTO mapDistrictToDistrictDTO(District District) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        DistrictDTO dto = modelMapper.map(District, DistrictDTO.class);
        return dto;
    }
}
