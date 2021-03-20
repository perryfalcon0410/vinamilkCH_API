package vn.viettel.common.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.common.repository.ProvinceRepository;
import vn.viettel.common.service.ProvinceService;
import vn.viettel.common.service.dto.ProvinceDTO;
import vn.viettel.common.specification.ProvinceSpecification;
import vn.viettel.core.db.entity.Province;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProvinceServiceImpl extends BaseServiceImpl<Province, ProvinceRepository> implements ProvinceService {
    @Override
    public Response<Page<ProvinceDTO>> index(String searchKeywords, Pageable pageable) {
        Response<Page<ProvinceDTO>> response = new Response<>();
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        Page<Province> provinces;

        provinces = repository.findAll(Specification.where(ProvinceSpecification.hasName(searchKeywords)), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<ProvinceDTO> dtos = provinces.map(this::mapProvinceToProvinceDTO);

        return response.withData(dtos);
    }

    @Override
    public Response<List<ProvinceDTO>> getAllProvinceByAreaIds(List<Long> areaIds) {
        Response<List<ProvinceDTO>> response = new Response<>();

        List<Province> provinces = repository.findProvincesByAreaIdInAndDeletedAtIsNull(areaIds);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<ProvinceDTO> dtos = provinces.stream().map(province -> modelMapper.map(province, ProvinceDTO.class)).collect(Collectors.toList());

        return response.withData(dtos);
    }

    private ProvinceDTO mapProvinceToProvinceDTO(Province area) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProvinceDTO dto = modelMapper.map(area, ProvinceDTO.class);
        return dto;
    }
}
