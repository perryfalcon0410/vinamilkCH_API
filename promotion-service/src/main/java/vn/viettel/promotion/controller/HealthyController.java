package vn.viettel.promotion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.util.Constants;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HealthyController {

    @GetMapping(value = { "/promotions/url"})
    public String getURLValue(HttpServletRequest request){
        return Constants.SERVICE_ALIVE;
    }
}
