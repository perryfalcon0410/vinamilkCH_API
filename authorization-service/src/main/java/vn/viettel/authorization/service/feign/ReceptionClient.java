package vn.viettel.authorization.service.feign;

import vn.viettel.core.dto.booking.BookingHistoryResponseDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClientAuthenticate(name = "reception-service")
public interface ReceptionClient {

    @PostMapping("/feignFindBookingHairPhotoByCustomerIds")
    List<BookingHistoryResponseDTO> feignFindBookingHairPhotoByCustomerIds(@RequestBody Map<String, Object> body);

}
