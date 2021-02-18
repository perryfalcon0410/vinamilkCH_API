package vn.viettel.authorization.controller;

import vn.viettel.authorization.service.TokenGenerateService;
import vn.viettel.core.security.anotation.RoleFeign;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    TokenGenerateService tokenGenerateService;

    @RoleFeign
    @RequestMapping(value = "/feignGenerateContinueToken", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String generateContinueToken(@RequestBody DefaultClaims claims) {
        return tokenGenerateService.createToken(claims);
    }

    @RoleFeign
    @GetMapping("/feignSave")
    public void storeToken(@RequestParam("token") String token) {
        tokenGenerateService.saveToken(token);
    }

    @RoleFeign
    @GetMapping("/feignGetBlackListToken")
    public boolean getBlackListToken(@RequestParam("token") String token) {
        return tokenGenerateService.getBlackListToken(token);
    }
}
