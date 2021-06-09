package vn.viettel.core.validation.annotation;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.validator.EmailAddressValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailAddressValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailAddress {

    String message() default "email address not correct";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ResponseMessage responseMessage();

}
