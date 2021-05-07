package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.SettingReservationMethod;

import javax.persistence.AttributeConverter;

public class SettingReservationMethodConverter implements AttributeConverter<SettingReservationMethod, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SettingReservationMethod method) {
        return method.getId();
    }

    @Override
    public SettingReservationMethod convertToEntityAttribute(Integer id) {
        return SettingReservationMethod.valueOf(id);
    }
}
