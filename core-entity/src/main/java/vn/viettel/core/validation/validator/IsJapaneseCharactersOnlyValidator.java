package vn.viettel.core.validation.validator;

import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.IsJapaneseCharactersOnly;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsJapaneseCharactersOnlyValidator implements ConstraintValidator<IsJapaneseCharactersOnly, CharSequence> {

    private static final String KEYBOARD_REGEX = "([一-龠]+|[ぁ-ゔ]+|[ァ-ヴー]+|[ａ-ｚＡ-Ｚ０-９]+|[々〆〤]+)";
    private ResponseMessage message;

    @Override
    public void initialize(IsJapaneseCharactersOnly constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CharSequence cs, ConstraintValidatorContext context) {
        String text = StringUtils.defaultString(cs.toString(), StringUtils.EMPTY);
        if (text.replaceAll(KEYBOARD_REGEX, StringUtils.EMPTY).trim().length()!=0) {
            throw new ValidateException(message);
        }
        return true;
    }
}
