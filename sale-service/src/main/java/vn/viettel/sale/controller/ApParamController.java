package vn.viettel.sale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.db.entity.common.ApParam;
import vn.viettel.core.db.entity.common.CategoryData;
import vn.viettel.sale.service.ApParamService;

import java.util.Optional;

@RestController
@RequestMapping("/api/sale")
public class ApParamController extends BaseController {
    @Autowired
    ApParamService apParamService;

    @GetMapping("/apparam/findById/{id}")
    public Optional<ApParam> getApParamById(@PathVariable Long id) {
        return apParamService.getApParamById(id);
    }
}
