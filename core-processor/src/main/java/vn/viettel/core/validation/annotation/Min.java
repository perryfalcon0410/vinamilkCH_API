package vn.viettel.core.validation.annotation;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.validator.MinValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MinValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
    String message() default "number not correct";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int value();

    ResponseMessage responseMessage();

}

