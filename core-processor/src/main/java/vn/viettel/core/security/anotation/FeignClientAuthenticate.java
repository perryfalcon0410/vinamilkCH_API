package vn.viettel.core.security.anotation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AliasFor;
import vn.viettel.core.security.config.FeignClientAuthenticateConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@FeignClient
public @interface FeignClientAuthenticate {

    @AliasFor(annotation = FeignClient.class)
    String name();

    @AliasFor(annotation = FeignClient.class, attribute = "configuration")
    Class<?>[] configuration() default FeignClientAuthenticateConfig.class;
}
