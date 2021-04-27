package vn.viettel.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.common.service.ApParamService;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.dto.common.ApParamDTO;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.security.anotation.RoleAdmin;
import vn.viettel.core.security.anotation.RoleFeign;

import java.util.List;

@RestController
public class ApParamController extends BaseController {
    @Autowired
    ApParamService apParamService;
    private final String root = "/commons/apparams";

    @GetMapping(value = { V1 + root + "/{id}"})
    public Response<ApParamDTO> getApParamById(@PathVariable Long id) {
        return apParamService.getApParamById(id);
    }

    @GetMapping(value = { V1 + root + "/cardtypes"})
    Response<List<ApParamDTO>> getCardTypes()
    {
        return apParamService.getCardTypes();
    }

    @GetMapping(value = { V1 + root + "/closelytypes"})
    Response<List<ApParamDTO>> getCloselytypes(){
        return apParamService.getCloselytypes();
    }
    @GetMapping("/reason-adjust/{id}")
    Response<ApParamDTO> getReasonAdjust(@PathVariable Long id){
        return apParamService.getReason(id);
    }

    @RoleAdmin
    @GetMapping(value = { V1 + root + "/type/{type}"})
    Response<List<ApParamDTO>> getByType(@PathVariable String type){
        return apParamService.getByType(type);
    }
    @RoleAdmin
    @GetMapping(value = { V1 + root + "/sale-mt-deny"})
    Response<List<ApParamDTO>> getReasonNotImport() {
        return apParamService.getReasonNotImport();
    }
    
    @RoleFeign
    @RoleAdmin
    @GetMapping(value = {V1 + root})
    public Response<List<ApParamDTO>> getApParams() {
        return apParamService.findAll();
    }

    @RoleFeign
    @RoleAdmin
    @GetMapping(value = {V1 + root + "/getByCode/{code}"})
    public Response<ApParamDTO> getApParamByCode(@PathVariable String code) {
        return apParamService.getByCode(code);
    }
}

