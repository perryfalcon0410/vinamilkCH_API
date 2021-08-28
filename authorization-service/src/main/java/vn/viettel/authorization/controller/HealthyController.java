package vn.viettel.authorization.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.util.Constants;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HealthyController {
    @GetMapping(value = { "users/url"})
    public String getURLValue(HttpServletRequest request){
        return Constants.SERVICE_ALIVE;
    }
}
