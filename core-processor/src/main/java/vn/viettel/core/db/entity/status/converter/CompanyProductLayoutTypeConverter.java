package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.CompanyProductLayoutType;

import javax.persistence.AttributeConverter;

public class CompanyProductLayoutTypeConverter implements AttributeConverter<CompanyProductLayoutType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CompanyProductLayoutType type) {
        return type == null ? null : type.getId();
    }

    @Override
    public CompanyProductLayoutType convertToEntityAttribute(Integer id) {
        return CompanyProductLayoutType.valueOf(id);
    }
}
