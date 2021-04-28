package vn.viettel.core.security.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor(){
        System.out.println("hee");
        return Optional.of("Admin");
    }
}
