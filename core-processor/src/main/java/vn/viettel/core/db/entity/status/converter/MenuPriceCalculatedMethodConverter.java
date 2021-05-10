package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.MenuPriceCalculatedMethod;

import javax.persistence.AttributeConverter;

public class MenuPriceCalculatedMethodConverter implements AttributeConverter<MenuPriceCalculatedMethod, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MenuPriceCalculatedMethod method) {
        return method.getId();
    }

    @Override
    public MenuPriceCalculatedMethod convertToEntityAttribute(Integer id) {
        return MenuPriceCalculatedMethod.valueOf(id);
    }
}
