package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.core.messaging.Response;
import vn.viettel.sale.service.ApParamService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sale")
public class ApParamController extends BaseController {
    @Autowired
    ApParamService apParamService;

    @GetMapping("/apparam/findById/{id}")
    public Response<ApParam> getApParamById(@PathVariable Long id) {
        return apParamService.getApParamById(id);
    }

    @GetMapping("/apparam/findAllByType")
    public Response<List<ApParam>> getAllByType(@RequestParam(value = "type") String type) {
        return apParamService.getAllByType(type);
    }
}
