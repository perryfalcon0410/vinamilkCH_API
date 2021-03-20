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
import vn.viettel.common.service.ProvinceService;
import vn.viettel.common.service.dto.ProvinceDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/common/province")
public class ProvinceController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    ProvinceService service;

    @RoleAdmin
    @GetMapping("/index")
    public Response<Page<ProvinceDTO>> index(@RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        logger.info("[index()] - province index #user_id: {}, #searchKeywords: {}", this.getUserId(), searchKeywords);
        return service.index(searchKeywords, pageable);
    }

    @RoleAdmin
    @GetMapping("/find-by-area-ids")
    public Response<List<ProvinceDTO>> getAllProvinceByAreaIds(@Valid @RequestParam(value = "areaIds") List<Long> areaIds) {
        logger.info("[getAllProvinceByAreaIds()] - area index #user_id: {}, #areaIds: {}", this.getUserId(), areaIds);
        return service.getAllProvinceByAreaIds(areaIds);
    }
}
