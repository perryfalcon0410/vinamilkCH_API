package vn.viettel.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.NotEmpty;

public class NotEmptyValidatorForArray implements ConstraintValidator<NotEmpty, Object[]> {

	ResponseMessage message;

	@Override
	public void initialize(NotEmpty constraintAnnotation) {
		this.message = constraintAnnotation.responseMessage();
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(Object[] array, ConstraintValidatorContext context) {
		if (array == null || array.length < 1) {
			throw new ValidateException(message);
		}
		return true;
	}

}
