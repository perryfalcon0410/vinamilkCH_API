package vn.viettel.customer.controller;

import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HealthyController extends BaseController {
    private final String root = "/customers/url";

    @GetMapping(value = { V1 + root})
    public String getURLValue(HttpServletRequest request){
        return request.getServerName() + ":" + request.getServerPort();
    }
}
