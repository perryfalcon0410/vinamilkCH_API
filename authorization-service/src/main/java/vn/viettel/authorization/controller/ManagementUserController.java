package vn.viettel.authorization.controller;

import vn.viettel.authorization.service.ManagementUserService;
import vn.viettel.core.db.entity.ManagementUsers;
import vn.viettel.core.dto.common.BodyDTO;
import vn.viettel.core.dto.salon.SalonConfirmationHairdresserDetailDTO;
import vn.viettel.core.dto.salon.SalonHairdresserResponseDTO;
import vn.viettel.core.security.anotation.RoleFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managementUser")
public class ManagementUserController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ManagementUserService managementUserService;

    @RoleFeign
    @GetMapping("/findAllHairdressersInSalon")
    public List<SalonHairdresserResponseDTO> getAllHairdressersInSalon(
            @RequestParam("salonId") Long salonId) {
        logger.info("[getAllHairdressersInSalon()] - get hairdressers (beautician, partime) in salon #salon: {}", salonId);
        return managementUserService.getAllHairdressersInSalon(salonId);
    }

    @RoleFeign
    @GetMapping("/getHairdresserById")
    public SalonHairdresserResponseDTO getHairdresserById(
            @RequestParam("hairdresserId") Long hairdresserId) {
        logger.info("[getHairdresserById()] - get hairdresser by #hairdresserId: {}", hairdresserId);
        return managementUserService.getHairdresserById(hairdresserId);
    }

    @RoleFeign
    @PostMapping("/feignGetHairdressersByIds")
    public List<SalonConfirmationHairdresserDetailDTO> feignGetHairdressersByIds(@RequestBody BodyDTO body) {
        logger.info("[feignGetHairdressersByIds()] - get hairdressers by #hairdresserIds: {}", body.getIds());
        return managementUserService.feignGetHairdressersByIds(body.getIds());
    }

    @RoleFeign
    @PostMapping("/feignGetHairdressersEntityByIds")
    public List<ManagementUsers> feignGetHairdressersEntityByIds(@RequestBody BodyDTO body) {
        logger.info("[feignGetHairdressersEntityByIds()] - get hairdressers entity by #hairdresserIds: {}", body.getIds());
        return managementUserService.feignGetHairdressersEntityByIds(body.getIds());
    }

    @RoleFeign
    @GetMapping("/getById")
    public ManagementUsers getManagementUserById(@RequestParam("id") Long id) {
        logger.info("[getManagementUserById() - get management by id #id: {}]", id);
        return managementUserService.getManagementUserById(id);
    }
}
