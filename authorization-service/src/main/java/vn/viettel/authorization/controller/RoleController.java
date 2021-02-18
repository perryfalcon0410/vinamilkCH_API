package vn.viettel.authorization.controller;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.RoleService;
import vn.viettel.authorization.service.dto.RoleDTO;
import vn.viettel.core.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class RoleController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    RoleService roleService;

    //@RoleAdministrator
    @GetMapping("/api/role/get-list-role")
    public Response<List<RoleDTO>> getListRole() {
        logger.info("[getListRole()] - get list role");
        return roleService.getListRole();
    }
}
