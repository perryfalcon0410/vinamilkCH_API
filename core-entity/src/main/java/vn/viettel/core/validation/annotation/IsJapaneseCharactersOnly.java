package vn.viettel.core.validation.annotation;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.validator.IsJapaneseCharactersOnlyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsJapaneseCharactersOnlyValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsJapaneseCharactersOnly {

    String message() default "must contains only japanese characters only";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ResponseMessage responseMessage();

}
