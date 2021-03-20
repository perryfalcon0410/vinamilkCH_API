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
import vn.viettel.common.service.DistrictService;
import vn.viettel.common.service.dto.DistrictDTO;
import vn.viettel.common.service.dto.ProvinceDTO;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/common/district")
public class DistrictController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    DistrictService service;

    @RoleAdmin
    @GetMapping("/index")
    public Response<Page<DistrictDTO>> index(@RequestParam(value = "searchKeywords", required = false) String searchKeywords, Pageable pageable) {
        logger.info("[index()] - district index #user_id: {}, #searchKeywords: {}", this.getUserId(), searchKeywords);
        return service.index(searchKeywords, pageable);
    }

    @RoleAdmin
    @GetMapping("/find-by-province-ids")
    public Response<List<ProvinceDTO>> getAllDistrictByAreaIds(@Valid @RequestParam(value = "provinceIds") List<Long> provinceIds) {
        logger.info("[getAllDistrictByAreaIds()] - district index #user_id: {}, #provinceIds: {}", this.getUserId(), provinceIds);
        return service.getAllDistrictByAreaIds(provinceIds);
    }
}
