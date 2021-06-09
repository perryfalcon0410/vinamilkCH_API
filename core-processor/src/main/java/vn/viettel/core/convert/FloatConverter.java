package vn.viettel.core.convert;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class FloatConverter extends AbstractSingleValueConverter {
    @Override
    public boolean canConvert(Class type) {
        return type.equals(Float.class);
    }

    @Override
    public Object fromString(String dtz) {
        if(dtz.length() == 0)
            return null;
        else
            return Float.parseFloat(dtz);
    }
}
