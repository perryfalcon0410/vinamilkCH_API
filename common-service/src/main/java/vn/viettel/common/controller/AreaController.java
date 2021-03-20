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
import vn.viettel.common.service.AreaService;
import vn.viettel.common.service.dto.AreaDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/common/area")
public class AreaController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    AreaService service;

    @RoleAdmin
    @GetMapping("/index")
    public Response<Page<AreaDTO>> index(@RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        logger.info("[index()] - area index #user_id: {}, #searchKeywords: {}", this.getUserId(), searchKeywords);
        return service.index(searchKeywords, pageable);
    }

    @RoleAdmin
    @GetMapping("/find-by-country-ids")
    public Response<List<AreaDTO>> getAllByCountryIds(@Valid @RequestParam(value = "countryIds") List<Long> countryIds) {
        logger.info("[getAllByCountryIds()] - area index #user_id: {}, #countryId: {}", this.getUserId(), countryIds);
        return service.getAllAreaByCountryIds(countryIds);
    }
}
