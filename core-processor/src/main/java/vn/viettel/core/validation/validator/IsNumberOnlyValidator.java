package vn.viettel.core.validation.validator;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.IsNumberOnly;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsNumberOnlyValidator implements ConstraintValidator<IsNumberOnly, CharSequence> {

    private static final String KEYBOARD_REGEX = "([０-９]+|[0-9]+)";
    private ResponseMessage message;

    @Override
    public void initialize(IsNumberOnly constraintAnnotation) {
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
