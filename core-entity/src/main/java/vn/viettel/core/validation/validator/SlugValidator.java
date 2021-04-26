package vn.viettel.core.validation.validator;

import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.validation.annotation.Slug;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SlugValidator implements ConstraintValidator<Slug, CharSequence> {

    private static final String EMAIL_REGEX = "^[a-z0-9]+(?:-[a-z0-9]+)*$";
    private ResponseMessage message;

    @Override
    public void initialize(Slug constraintAnnotation) {
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
