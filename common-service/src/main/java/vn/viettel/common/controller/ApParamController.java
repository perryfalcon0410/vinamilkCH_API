package vn.viettel.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;

import java.util.List;

@RestController
@RequestMapping("/api/sale/apparam")
public class ApParamController extends BaseController {
    @Autowired
    ApParamService apParamService;

    @GetMapping("/{id}")
    public Response<ApParamDTO> getApParamById(@PathVariable Long id) {
        return apParamService.getApParamById(id);
    }

    @GetMapping("/cardtypes")
    Response<List<ApParamDTO>> getCardTypes()
    {
        return apParamService.getCardTypes();
    }

    @GetMapping("/closelytypes")
    Response<List<ApParamDTO>> getCloselytypes(){
        return apParamService.getCloselytypes();
    }

}

