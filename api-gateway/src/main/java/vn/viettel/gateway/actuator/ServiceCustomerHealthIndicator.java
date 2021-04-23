package vn.viettel.gateway.actuator;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceCustomerHealthIndicator implements HealthIndicator {

//        private static final String URL
//                = "http://localhost:2407/api/customers";
    RestTemplate restTemplate = new RestTemplate();

    @Override
    public Health health() {
        // check if url shortener service url is reachable
//            try (Socket socket =
//                         new Socket(new java.net.URL(URL).getHost(),9003)) {
//            } catch (Exception e) {
////                log.warn("Failed to connect to: {}",URL);
//                return Health.down()
//                        .withDetail("error", e.getMessage())
//                        .build();
//            }
//            return Health.up().withDetail("customer-service", "causion").build();

        try {
            JsonNode resp = restTemplate.getForObject("http://localhost:9003/actuator/health", JsonNode.class);
            if (resp.get("status").asText().equalsIgnoreCase("UP")) {
                return Health.up().withDetail("Customer-service", "Available").build();
            }
        } catch (Exception ex) {
            return Health.down(ex).withDetail("Customer-service", "Not available").build();
        }
        return Health.down().withDetail("Customer-service", "Not available").build();
    }
}
