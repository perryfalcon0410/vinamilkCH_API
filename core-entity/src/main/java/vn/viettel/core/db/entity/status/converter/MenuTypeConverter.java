package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.MenuType;

import javax.persistence.AttributeConverter;

public class MenuTypeConverter implements AttributeConverter<MenuType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MenuType menuType) {
        return menuType.getId();
    }

    @Override
    public MenuType convertToEntityAttribute(Integer id) {
        return MenuType.valueOf(id);
    }
}
