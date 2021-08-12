package vn.viettel.customer.controller;

import org.springframework.web.bind.annotation.*;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.util.Constants;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HealthyController {
    private final String root = "/customers/url";

    @GetMapping(value = { root})
    public String getURLValue(HttpServletRequest request){
        return Constants.SERVICE_ALIVE;
    }
}
