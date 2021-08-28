package vn.viettel.core.validation.annotation;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.validator.NotEmptyValidatorForArray;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotEmptyValidatorForArray.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmpty {
    String message() default "must be not empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ResponseMessage responseMessage();
}
