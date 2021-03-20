package vn.viettel.common.service.impl;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.viettel.common.repository.CountryRepository;
import vn.viettel.common.service.CountryService;
import vn.viettel.common.service.dto.CountryDTO;
import vn.viettel.common.specification.CountrySpecification;
import vn.viettel.core.db.entity.Country;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;

@Service
public class CountryServiceImpl extends BaseServiceImpl<Country, CountryRepository> implements CountryService {

    @Override
    public Response<Page<CountryDTO>> index(String searchKeywords, Pageable pageable) {
        Response<Page<CountryDTO>> response = new Response<>();
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);

        Page<Country> countries;

        countries = repository.findAll(Specification.where(CountrySpecification.hasName(searchKeywords)), pageable);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Page<CountryDTO> dtos = countries.map(this::mapCountryToCountryDTO);

        return response.withData(dtos);
    }

    private CountryDTO mapCountryToCountryDTO(Country country) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CountryDTO dto = modelMapper.map(country, CountryDTO.class);
        return dto;
    }
}
