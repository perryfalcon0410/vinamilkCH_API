package vn.viettel.authorization.controller;

import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.viettel.authorization.service.TokenGenerateService;
import vn.viettel.core.controller.BaseController;

@RestController
public class TokenController extends BaseController {

    @Autowired
    TokenGenerateService tokenGenerateService;
    private final String root = "/token";

    @RequestMapping(value = { V1 + root + "/feignGenerateContinueToken"}, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String generateContinueToken(@RequestBody DefaultClaims claims) {
        return tokenGenerateService.createToken(claims);
    }

    @GetMapping("/feignSave")
    public void storeToken(@RequestParam("token") String token) {
        tokenGenerateService.saveToken(token);
    }

    @GetMapping("/feignGetBlackListToken")
    public boolean getBlackListToken(@RequestParam("token") String token) {
        return tokenGenerateService.getBlackListToken(token);
    }
}
