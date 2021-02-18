package vn.viettel.authorization.service.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import vn.viettel.authorization.service.UserService;
import vn.viettel.authorization.service.validation.annotation.UserExist;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;

public class UserExistValidator implements ConstraintValidator<UserExist, Long> {

    @Autowired
    UserService service;

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        if (userId != null && !service.exists(userId)) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        return true;
    }

}
