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
import vn.viettel.common.service.CountryService;
import vn.viettel.common.service.dto.CountryDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;


@RestController
@RequestMapping("api/common/country")
public class CountryController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    CountryService service;

    @RoleAdmin
    @GetMapping("/index")
    public Response<Page<CountryDTO>> getCountries(@RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        logger.info("[index()] - country index #user_id: {}, #searchKeywords: {}", this.getUserId(), searchKeywords);
        return service.index(searchKeywords, pageable);
    }
}
