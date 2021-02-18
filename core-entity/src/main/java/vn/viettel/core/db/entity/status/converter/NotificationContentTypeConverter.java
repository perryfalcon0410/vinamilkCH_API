package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.NotificationContentType;

import javax.persistence.AttributeConverter;

public class NotificationContentTypeConverter implements AttributeConverter<NotificationContentType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NotificationContentType type) {
        return type == null ? null : type.getId();
    }

    @Override
    public NotificationContentType convertToEntityAttribute(Integer id) {
        return NotificationContentType.valueOf(id);
    }
}
