package vn.viettel.gateway.actuator;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.viettel.core.util.Constants;
import vn.viettel.gateway.service.UrlService;

@Component
public class AuthHealthIndicator implements HealthIndicator {

    private static String service_name = "authorization-service";
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UrlService urlService;

    @Override
    public Health health() {
        try {
            if (urlService.getAuthorizationUrl().equalsIgnoreCase(Constants.SERVICE_ALIVE)) {
                return Health.up().withDetail(service_name, "Available").build();
            }
        }catch (Exception ex){
            return Health.down(ex).withDetail(service_name, "Not available").build();
        }

        return Health.down().withDetail(service_name, "Not available").build();
    }
}
