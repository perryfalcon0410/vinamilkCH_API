package vn.viettel.authorization.controller;

import vn.viettel.core.dto.user.*;
import vn.viettel.core.dto.user.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.ChannelService;
import vn.viettel.core.db.entity.Channel;
import vn.viettel.core.handler.HandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/channel")
public class ChannelController extends HandlerException {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ChannelService service;

    @PostMapping("/getChannelByType")
    public Response<List<Channel>> getChannelByType(@RequestBody Map<String, Object> body) {
        logger.info("[getChannelByType()] - #body: {}", body.get("type"));
        return service.getChannelByType(body.get("type"));
    }

    @GetMapping("/getMemberChannel")
    public Response<MemberInformationResponseDTO> getMemberChannel() {
        return service.getMemberChannel();
    }

    @GetMapping("/getCitiesByPrefectureId")
    public Response<CityDTO> getCitiesByPrefectureId(@RequestParam("prefectureId") Long prefectureId) {
        logger.info("[getCitiesByPrefectureId()] - #prefectureId: {}", prefectureId);
        return service.getCitiesByPrefectureId(prefectureId);
    }

    @GetMapping("/getPlacesByCityId")
    public Response<PlaceDTO> getPlacesByCityId(@RequestParam("cityId") Long cityId) {
        logger.info("[getPlacesByCityId()] - #cityId: {}", cityId);
        return service.getPlacesByCityId(cityId);
    }

    @GetMapping("/getPrefectureByRegionId")
    public Response<PrefectureDTO> getPrefectureByRegionId(@RequestParam("regionId") Long regionId) {
        logger.info("[getPrefectureByRegionId()] - #regionId: {}", regionId);
        return service.getPrefectureByRegionId(regionId);
    }

    @GetMapping("/getRegionsByCountryId")
    public Response<RegionDTO> getRegionsByCountryId(@RequestParam("countryId") Long countryId) {
        logger.info("[getRegionsByCountryId()] - #countryId: {}", countryId);
        return service.getRegionsByCountryId(countryId);
    }

    @GetMapping("/getPlaceByPostalCode")
    public Response<List<FilledPlacesResponseDTO>> getPlaceByPostalCode(@RequestParam("postalCode") String postalCode) {
        logger.info("[getPlaceByPostalCode()] - #postalCode: {}", postalCode);
        return service.getPlaceByPostalCode(postalCode);
    }
}
