package vn.viettel.authorization.config;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import vn.viettel.authorization.repository.TokenRepository;
import vn.viettel.core.db.entity.Token;
import vn.viettel.core.dto.company.CompanyFeatureListDTO;
import vn.viettel.core.service.feign.CompanyClient;
import vn.viettel.core.service.feign.UserClient;
import vn.viettel.core.service.mapper.CustomModelMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new CustomModelMapper();
    }

    // try added to run
    @Autowired
    TokenRepository tokenRepository;

    @Bean
    UserClient userClient() {
        return new UserClient() {
            @Override
            public String generateContinueToken(Claims claims) {
                return null;
            }

            @Override
            public void storeToken(String token) {

            }

            @Override
            public boolean getBlackListToken(String token) {
                Token tokenObj = tokenRepository.findByToken(token);
                if (tokenObj != null) {
                    return true;
                }
                return false;
            }
        };
    }

    @Bean
    CompanyClient companyClient() {
        return new CompanyClient() {
            @Override
            public List<CompanyFeatureListDTO> feignGetAvailableFeatureListByCompanyId(Long companyId) {
                return null;
            }
        };
    }

}
