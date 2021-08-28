package vn.viettel.common.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.viettel.core.controller.BaseController;
import vn.viettel.core.messaging.Response;

@RestController
public class DbLogController extends BaseController {

    private final String root = "/dblogs";

    @PostMapping( value = { V1 + root })
    public Response<Object> create() {
        return new Response<>();
    }
}
