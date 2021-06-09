package vn.viettel.core.validation.validator;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.EmailAddress;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailAddressValidator implements ConstraintValidator<EmailAddress, CharSequence> {

    private static final String EMAIL_REGEX = "([A-Za-z0-9-_.]+@[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9]+)+)";
    private ResponseMessage message;

    @Override
    public void initialize(EmailAddress constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CharSequence cs, ConstraintValidatorContext context) {
        CharSequence email = StringUtils.defaultIfBlank(cs, StringUtils.EMPTY);
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new ValidateException(message);
        }
        return true;
    }
}
