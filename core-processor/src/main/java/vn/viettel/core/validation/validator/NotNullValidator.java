package vn.viettel.core.validation.validator;

import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.util.ResponseMessage;
import vn.viettel.core.validation.annotation.NotNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullValidator implements ConstraintValidator<NotNull, Object> {

    ResponseMessage message;

    @Override
    public void initialize(NotNull constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            throw new ValidateException(message);
        }
        return true;
    }

   /*  UserRole[] includeRoles;

   UserRole[] excludeRoles;

    @Autowired
    SecurityContexHolder context;

    @Override
    public void initialize(NotNull constraintAnnotation) {
        this.message = constraintAnnotation.responseMessage();
        this.includeRoles = constraintAnnotation.includeRoles();
        this.excludeRoles = constraintAnnotation.excludeRoles();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

  @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        boolean valid;
        if (ArrayUtils.isNotEmpty(includeRoles)) {
            valid = isValidIncludeRole(object);
        } else if (ArrayUtils.isNotEmpty(excludeRoles)) {
            valid = isValidExcludeRole(object);
        } else {
            valid = isValidAll(object);
        }

        if (!valid) {
            throw new ValidateException(message);
        }
        return true;
    }

    private boolean isValidIncludeRole(Object object) {
        String role = context.getContext().getRole();
        boolean matchIncludeRole = Arrays.stream(includeRoles)
            .map(UserRole::value)
            .anyMatch(role::equalsIgnoreCase);

        boolean valid = true;
        if (matchIncludeRole && object == null) {
            valid = false;
        }
        return valid;
    }

    private boolean isValidExcludeRole(Object object) {
        String role = context.getContext().getRole();
        boolean noneMatchExcludeRole = Arrays.stream(excludeRoles)
            .map(UserRole::value)
            .noneMatch(role::equalsIgnoreCase);

        boolean valid = true;
        if (noneMatchExcludeRole && object == null) {
            valid = false;
        }
        return valid;
    }

    private boolean isValidAll(Object object) {
        return object != null;
    }
*/
}
