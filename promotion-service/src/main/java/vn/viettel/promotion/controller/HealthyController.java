package vn.viettel.promotion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HealthyController extends BaseController {

    @GetMapping(value = { V1 + "/promotions/url"})
    public String getURLValue(HttpServletRequest request){
        return request.getServerName() + ":" + request.getServerPort();
    }
}
