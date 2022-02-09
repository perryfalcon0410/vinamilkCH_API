package vn.viettel.sale.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.viettel.core.util.Constants;

@RestController
public class HealthyController {

    @GetMapping(value = { "/sales/url"} )
    public String getURLValue(HttpServletRequest request){
        return Constants.SERVICE_ALIVE;
    }
}
