package vn.viettel.core.validation.validator;

import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.Min;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinValidator implements ConstraintValidator<Min, Integer> {
    ResponseMessage message;
    int value;

    @Override
    public void initialize(Min constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        this.value = constraintAnnotation.value();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value != null && value < this.value) {
            throw new ValidateException(message);
        }
        return true;
    }

}
