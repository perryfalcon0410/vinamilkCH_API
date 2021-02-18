package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.NotificationMediaActionType;

import javax.persistence.AttributeConverter;

public class NotificationMediaActionTypeConverter implements AttributeConverter<NotificationMediaActionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NotificationMediaActionType type) {
        return type == null ? null : type.getId();
    }

    @Override
    public NotificationMediaActionType convertToEntityAttribute(Integer id) {
        return NotificationMediaActionType.valueOf(id);
    }
}
