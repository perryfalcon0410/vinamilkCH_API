package vn.viettel.authorization.service.feign;

import vn.viettel.core.db.entity.Salon;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClientAuthenticate(name = "salon-service")
public interface SalonClient {

    @GetMapping("/getExistedSalon")
    public Salon getExistedSalon(@RequestParam("salonId") Long salonId);

    @GetMapping("/getExistedSalonBySlug")
    public Salon getExistedSalonBySlug(@RequestParam("slug") String slug);

}
