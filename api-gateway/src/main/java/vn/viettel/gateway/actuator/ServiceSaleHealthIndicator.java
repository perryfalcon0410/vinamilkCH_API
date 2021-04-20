package vn.viettel.gateway.actuator;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceSaleHealthIndicator implements HealthIndicator {

    RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        try {
            JsonNode resp = restTemplate.getForObject("http://localhost:9006/actuator/health", JsonNode.class);
            if (resp.get("status").asText().equalsIgnoreCase("UP")) {
                return Health.up().withDetail("Sale-service", "Available").build();
            }
        } catch (Exception ex) {
            return Health.down(ex).withDetail("Sale-service", "Not available").build();
        }
        return Health.down().withDetail("Sale-service", "Not available").build();
    }
}
