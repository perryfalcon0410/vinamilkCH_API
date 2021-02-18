package vn.viettel.authorization.service.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import vn.viettel.authorization.service.validation.validator.ShopExistValidator;

@Constraint(validatedBy = ShopExistValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShopExist {

    String message() default "shop does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
