package vn.viettel.authorization.service.impl;

import vn.viettel.authorization.repository.*;
import vn.viettel.core.dto.user.*;
import vn.viettel.core.db.entity.City;
import vn.viettel.core.db.entity.Place;
import vn.viettel.core.db.entity.Prefecture;
import vn.viettel.core.db.entity.Region;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.ChannelService;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.viettel.core.db.entity.commonEnum.Channel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelServiceImpl extends BaseServiceImpl<vn.viettel.core.db.entity.Channel, ChannelRepository> implements ChannelService {

    @Autowired
    PrefectureRepository prefectureRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    PlaceRepository placeRepository;

    private Response<List<vn.viettel.core.db.entity.Channel>> getChannelListByParentsAndChild(List<Long> ids, List<Long> parentIds) {
        Response<List<vn.viettel.core.db.entity.Channel>> result = new Response<>();

        List<vn.viettel.core.db.entity.Channel> channels = repository.findAllByIdInOrParentInAndDeletedAtIsNull(ids, parentIds);

        return result.withData(channels);
    }

    private Response<List<vn.viettel.core.db.entity.Channel>> getChannelListByType(List<Long> longTypes) {
        Response<List<vn.viettel.core.db.entity.Channel>> result = new Response<>();

        if (CollectionUtils.isEmpty(longTypes)) {
            return result.withError(ResponseMessage.CHANNEL_TYPE_MUST_NOT_NULL);
        }
        List<vn.viettel.core.db.entity.Channel> channels = repository.findAllByTypeInAndDeletedAtIsNull(longTypes);
        if (CollectionUtils.isEmpty(channels)) {
            return result.withError(ResponseMessage.CHANNEL_NOT_EXISTED);
        }

        return result.withData(channels);
    }

    @Override
    public Response<List<vn.viettel.core.db.entity.Channel>> getChannelByType(Object typeIds) {
        List<Integer> intTypes = (List<Integer>) typeIds;
        List<Long> longTypes = intTypes.stream().map(item -> Long.parseLong(item.toString())).collect(Collectors.toList());

        return this.getChannelListByType(longTypes);
    }

    @Override
    public List<ChannelTypeResponseDTO> getChannelListInformationMaster() {
        // 1. get channels
        // prepare search enum
        List<Long> parentIds = Arrays.asList(
                Channel.MEMBER_FAMILY_STRUCTURE.getId(),
                Channel.MEMBER_FAVORITE_STYLE.getId(),
                Channel.MEMBER_OCCUPATION.getId(),
                Channel.MEMBER_PROFESSION.getId(),
                Channel.MEMBER_WORRIES.getId(),
                Channel.MEMBER_KNOWN_SOURCE.getId()
        );
        List<vn.viettel.core.db.entity.Channel> channels = this.getChannelListByParentsAndChild(parentIds, parentIds).getData();

        if (channels == null) {
            return null;
        }

        return channels.stream().filter(item -> item.getParent() == null).map(item ->
                new ChannelTypeResponseDTO(
                        item.getId(),
                        item.getName(),
                        item.getType(),
                        channels.stream().filter(inside -> inside.getParent() != null && inside.getType().equals(item.getType()))
                                .map(inside -> modelMapper.map(inside, ChannelTypeResponseDTO.class)).collect(Collectors.toList()),
                        item.getParent(),
                        null
                )
        ).collect(Collectors.toList());
    }


    @Override
    public Response<MemberInformationResponseDTO> getMemberChannel() {
        Response<MemberInformationResponseDTO> result = new Response<>();
        MemberInformationResponseDTO data = new MemberInformationResponseDTO();

        // 1. Get prefectures info
        List<Prefecture> prefectures = prefectureRepository.findByDeletedAtIsNull();
        List<ChannelTypeResponseDTO> channelsResult = this.getChannelListInformationMaster();

        // 2. return screen value
        data.setChannels(channelsResult);
        data.setPrefectures(prefectures);

        return result.withData(data);
    }


    @Override
    public Response<CityDTO> getCitiesByPrefectureId(long prefectureId) {
        Response<CityDTO> result = new Response<>();
        CityDTO data = new CityDTO();

        List<City> cities = cityRepository.findAllByPrefectureId(prefectureId);
        data.setCities(cities);

        return result.withData(data);
    }

    @Override
    public Response<PlaceDTO> getPlacesByCityId(long cityId) {
        Response<PlaceDTO> result = new Response<>();
        PlaceDTO data = new PlaceDTO();

        List<Place> places = placeRepository.findAllByCityId(cityId);
        data.setPlaces(places);

        return result.withData(data);
    }

    @Override
    public Response<PrefectureDTO> getPrefectureByRegionId(long regionId) {
        Response<PrefectureDTO> result = new Response<>();
        PrefectureDTO data = new PrefectureDTO();

        List<Prefecture> prefectures = prefectureRepository.findAllByRegionId(regionId);
        data.setPrefectures(prefectures);

        return result.withData(data);
    }

    @Override
    public Response<RegionDTO> getRegionsByCountryId(long countryId) {
        Response<RegionDTO> result = new Response<>();
        RegionDTO data = new RegionDTO();

        // 1. Get prefectures info
        List<Region> regions = regionRepository.findAllByCountryId(countryId);
        // 2. return screen value
        data.setRegions(regions);

        return result.withData(data);
    }

    @Override
    public Response<List<FilledPlacesResponseDTO>> getPlaceByPostalCode(String postalCode) {
        Response<List<FilledPlacesResponseDTO>> result = new Response<List<FilledPlacesResponseDTO>>();

        List<FilledPlacesResponseDTO> places = placeRepository.getAllByPostalCode(postalCode);

        return result.withData(places);
    }
}
