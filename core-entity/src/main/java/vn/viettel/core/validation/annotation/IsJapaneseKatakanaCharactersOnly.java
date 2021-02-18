package vn.viettel.core.validation.annotation;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.validation.validator.IsJapaneseKatakanaCharactersOnlyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsJapaneseKatakanaCharactersOnlyValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsJapaneseKatakanaCharactersOnly {

    String message() default "must contains only japanese katakana characters only";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    ResponseMessage responseMessage();

}
