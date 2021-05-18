package vn.viettel.core.service.dto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@Target({ ElementType.TYPE })
@JsonIgnoreProperties(value = { "createdAt", "updatedAt" })
public @interface JsonIgnoreModifiedTimeProperties {
}
