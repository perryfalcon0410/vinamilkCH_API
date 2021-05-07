package vn.viettel.core.validation.validator;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.MinTextLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinTextLengthValidator implements ConstraintValidator<MinTextLength, CharSequence> {

    ResponseMessage message;
    int length;

    @Override
    public void initialize(MinTextLength constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        this.length = constraintAnnotation.length();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CharSequence cs, ConstraintValidatorContext context) {
        if (cs.length() < this.length) {
            throw new ValidateException(message);
        }
        return true;
    }
}
