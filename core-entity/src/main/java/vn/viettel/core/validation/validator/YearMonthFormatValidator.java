package vn.viettel.core.validation.validator;

import java.time.YearMonth;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.YearMonthFormat;

public class YearMonthFormatValidator implements ConstraintValidator<YearMonthFormat, Object> {

    ResponseMessage responseMessage;

    @Override
    public void initialize(YearMonthFormat constraintAnnotation) {
        this.responseMessage = constraintAnnotation.responseMessage();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
    	try {
	        if (object != null && YearMonth.parse(object.toString()) != null) {
	            return true;
	        }
        }catch (Exception e) {
        	throw new ValidateException(responseMessage);
		}
    	throw new ValidateException(responseMessage);
    }

}
