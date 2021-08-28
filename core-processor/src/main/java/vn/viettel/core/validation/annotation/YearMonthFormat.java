package vn.viettel.core.validation.annotation;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.validator.YearMonthFormatValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = YearMonthFormatValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface YearMonthFormat {

    String message() default "must be year month Format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ResponseMessage responseMessage();
}
