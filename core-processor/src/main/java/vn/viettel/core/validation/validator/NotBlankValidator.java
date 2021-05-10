package vn.viettel.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.NotBlank;

public class NotBlankValidator implements ConstraintValidator<NotBlank, CharSequence> {

	ResponseMessage message;

	@Override
	public void initialize(NotBlank constraintAnnotation) {
		this.message = constraintAnnotation.responseMessage();
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(CharSequence charSequence, ConstraintValidatorContext context) {
		if (StringUtils.isEmpty(charSequence)) {
			throw new ValidateException(message);
		}
		return true;
	}

}
