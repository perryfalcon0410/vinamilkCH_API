package vn.viettel.core.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditingConfig {

    @Autowired
    private SecurityContexHolder securityContexHolder;

     @Autowired
     HttpServletRequest request;

    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl();
    }

    private class AuditorAwareImpl implements AuditorAware<String> {
        @Override
        public Optional<String> getCurrentAuditor(){

            if(securityContexHolder != null && securityContexHolder.getContext() != null && securityContexHolder.getContext().getUserName() != null ) {
                String username = (String) request.getAttribute(Constants.CURRENT_USERNAME);
                if(username ==null  || username.isEmpty())  return Optional.of(securityContexHolder.getContext().getUserName());
                return Optional.of(username);
            }
            else  return Optional.of("NOT_LOGIN");
        }
    }
}



