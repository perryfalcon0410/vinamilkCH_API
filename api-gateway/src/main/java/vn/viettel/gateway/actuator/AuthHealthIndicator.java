package vn.viettel.gateway.actuator;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.viettel.gateway.service.UrlService;

@Component
public class AuthHealthIndicator implements HealthIndicator {

    private static String service_name = "Authorization-service";
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private UrlService urlService;

    @Override
    public Health health() {
        JsonNode resp = restTemplate.getForObject("https://" + urlService.getAuthorizationUrl() + "/actuator/health", JsonNode.class);
        if (resp.get("status").asText().equalsIgnoreCase("UP")) {
            return Health.up().withDetail(service_name, "Available").build();
        }

        return Health.down().withDetail(service_name, "Not available").build();
    }
}
