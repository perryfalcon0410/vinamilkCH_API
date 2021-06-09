package vn.viettel.core.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.validator.NotBlankValidator;

@Constraint(validatedBy = NotBlankValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlank {

    String message() default "must be not blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ResponseMessage responseMessage();
}
