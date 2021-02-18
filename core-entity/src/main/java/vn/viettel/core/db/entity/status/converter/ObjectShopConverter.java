package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.ObjectShop;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ObjectShopConverter implements AttributeConverter<ObjectShop, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ObjectShop objectShop) {
        return objectShop.getId();
    }

    @Override
    public ObjectShop convertToEntityAttribute(Integer id) {
        return ObjectShop.valueOf(id);
    }
}
