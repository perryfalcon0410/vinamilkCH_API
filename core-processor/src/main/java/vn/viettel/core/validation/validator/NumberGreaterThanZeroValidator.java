package vn.viettel.core.validation.validator;

import org.apache.commons.lang3.StringUtils;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.IsNumberOnly;
import vn.viettel.core.validation.annotation.NumberGreaterThanZero;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NumberGreaterThanZeroValidator implements ConstraintValidator<NumberGreaterThanZero, Number> {

    private ResponseMessage message;

    @Override
    public void initialize(NumberGreaterThanZero constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Number number, ConstraintValidatorContext context) {
        if (number == null || (number != null && number.intValue() <= 0)) {
            throw new ValidateException(message);
        }
        return true;
    }
}
