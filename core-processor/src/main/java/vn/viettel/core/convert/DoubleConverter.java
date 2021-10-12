package vn.viettel.core.convert;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class DoubleConverter extends AbstractSingleValueConverter {
    @Override
    public boolean canConvert(Class type) {
        return type.equals(Double.class);
    }

    @Override
    public Object fromString(String dtz) {
        if(dtz == null || dtz.isEmpty())
            return null;
        else
            return Double.parseDouble(dtz);
    }
}
