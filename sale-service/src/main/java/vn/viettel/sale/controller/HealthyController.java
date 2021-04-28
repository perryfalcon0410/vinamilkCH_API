package vn.viettel.sale.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HealthyController {

    @GetMapping(value = { "/sales/url"} )
    public String getURLValue(HttpServletRequest request){
        return request.getServerName() + ":" + request.getServerPort();
    }
}
