package vn.viettel.core.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import vn.viettel.core.security.context.SecurityContexHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditingConfig {

    @Autowired
    private SecurityContexHolder securityContexHolder;

    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl();
    }

    private class AuditorAwareImpl implements AuditorAware<String> {
        @Override
        public Optional<String> getCurrentAuditor(){
            if(securityContexHolder != null && securityContexHolder.getContext() != null )
                return Optional.of(securityContexHolder.getContext().getUserName());
            else  return Optional.of("NOT_LOGIN");
        }
    }
}



