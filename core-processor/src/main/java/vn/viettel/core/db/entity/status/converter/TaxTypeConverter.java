package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.TaxType;

import javax.persistence.AttributeConverter;

public class TaxTypeConverter implements AttributeConverter<TaxType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaxType taxType) {
        return taxType.getId();
    }

    @Override
    public TaxType convertToEntityAttribute(Integer id) {
        return TaxType.valueOf(id);
    }
}
