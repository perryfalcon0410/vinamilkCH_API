package vn.viettel.core.annotation;

import vn.viettel.core.db.entity.commonEnum.CustomJPAType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scalar {
    CustomJPAType type();
}
