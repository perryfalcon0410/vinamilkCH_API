package vn.viettel.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {
    @Value("${health.msg}")
    private String healthCheckMsg;

    @RequestMapping(method = RequestMethod.GET, value = "/gateway/healthCheck")
    public String healthCheck() {
        return healthCheckMsg;
    }

}
