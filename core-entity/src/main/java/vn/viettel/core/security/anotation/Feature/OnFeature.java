package vn.viettel.core.security.anotation.Feature;

import vn.viettel.core.db.entity.commonEnum.Feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface OnFeature {
    Feature feature();
}
