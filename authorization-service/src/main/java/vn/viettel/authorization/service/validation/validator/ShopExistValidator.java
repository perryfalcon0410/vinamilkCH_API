package vn.viettel.authorization.service.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import vn.viettel.authorization.service.feign.ShopClient;
import vn.viettel.authorization.service.validation.annotation.ShopExist;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;

public class ShopExistValidator implements ConstraintValidator<ShopExist, Long> {

    @Autowired
    ShopClient client;

    @Override
    public boolean isValid(Long shopId, ConstraintValidatorContext context) {
        if (shopId != null && !client.shopExist(shopId)) {
            throw new ValidateException(ResponseMessage.SALON_DOES_NOT_EXIST);
        }
        return true;
    }

}
