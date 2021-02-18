package vn.viettel.authorization.service.feign;

import vn.viettel.core.dto.ObjectSettingDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClientAuthenticate(name = "reservation-service")
public interface ObjectSettingClient {

    /* Get object setting by object and object id */
    @GetMapping("/object-setting/get-by-object-and-object-id")
    public ObjectSettingDTO getByObjectAndObjectId(@RequestParam("object") Integer object, @RequestParam("objectId") Long objectId);

    /* Check custom domain name exist */
    @GetMapping("/object-setting/check-custom-domain-name-exist")
    public Boolean checkCustomDomainExist(@RequestParam("customDomainName") String customDomainName);

}
