package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.Object;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ObjectConverter implements AttributeConverter<Object, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Object object) {
        return object.getId();
    }

    @Override
    public Object convertToEntityAttribute(Integer id) {
        return Object.valueOf(id);
    }
}
