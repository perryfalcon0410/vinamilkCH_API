package vn.viettel.core.validation.validator;

import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.MaxTextLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxTextLengthValidator implements ConstraintValidator<MaxTextLength, CharSequence> {

    ResponseMessage message;
    int length;

    @Override
    public void initialize(MaxTextLength constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        this.length = constraintAnnotation.length();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CharSequence cs, ConstraintValidatorContext constraintValidatorContext) {
        if (cs != null && cs.length() > this.length) {
            throw new ValidateException(message);
        }
        return true;
    }
}
