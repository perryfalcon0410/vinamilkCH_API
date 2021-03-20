package vn.viettel.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.WardService;
import vn.viettel.common.service.dto.ProvinceDTO;
import vn.viettel.common.service.dto.WardDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/common/ward")
public class WardController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    WardService service;

    @RoleAdmin
    @GetMapping("/index")
    public Response<Page<WardDTO>> index(@RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        logger.info("[index()] - Ward index #user_id: {}, #searchKeywords: {}", this.getUserId(), searchKeywords);
        return service.index(searchKeywords, pageable);
    }

    @RoleAdmin
    @GetMapping("/find-by-district-ids")
    public Response<List<WardDTO>> getAllWardByDistrictIds(@Valid @RequestParam(value = "districtIds") List<Long> districtIds) {
        logger.info("[getAllWardByDistrictIds()] - ward index #user_id: {}, #districtIds: {}", this.getUserId(), districtIds);
        return service.getAllWardByDistrictIds(districtIds);
    }
}
