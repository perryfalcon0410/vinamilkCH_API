package vn.viettel.authorization.service;

import vn.viettel.core.dto.user.*;
import vn.viettel.core.dto.user.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.db.entity.Channel;
import vn.viettel.core.service.BaseService;

import java.util.List;

public interface ChannelService extends BaseService {
    /**
     * get channel by typeIds
     *
     * @param typeIds object
     * @return
     */
    Response<List<Channel>> getChannelByType(Object typeIds);

    /**
     *
     * @return List<Channel>
     */
    Response<MemberInformationResponseDTO> getMemberChannel();


    Response<CityDTO> getCitiesByPrefectureId(long prefectureId);

    Response<PlaceDTO> getPlacesByCityId(long cityId);

    Response<PrefectureDTO> getPrefectureByRegionId(long regionId);

    Response<RegionDTO> getRegionsByCountryId(long countryId);

    Response<List<FilledPlacesResponseDTO>> getPlaceByPostalCode(String postalCode);



    /**
     * Common function to get channel list data
     * @return List<ChannelTypeResponseDTO>
     */
    List<ChannelTypeResponseDTO> getChannelListInformationMaster();
}
