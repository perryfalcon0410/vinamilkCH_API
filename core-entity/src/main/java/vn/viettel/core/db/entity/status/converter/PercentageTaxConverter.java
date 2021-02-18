package vn.viettel.core.db.entity.status.converter;

import vn.viettel.core.db.entity.status.PercentageTax;

import javax.persistence.AttributeConverter;

public class PercentageTaxConverter implements AttributeConverter<PercentageTax, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PercentageTax percentageTax) {
        return percentageTax.getId();
    }

    @Override
    public PercentageTax convertToEntityAttribute(Integer id) {
        return PercentageTax.valueOf(id);
    }
}
