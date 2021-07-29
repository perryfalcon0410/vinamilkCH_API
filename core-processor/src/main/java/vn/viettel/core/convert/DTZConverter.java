package vn.viettel.core.convert;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import vn.viettel.core.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DTZConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        return type.equals(LocalDateTime.class);
    }

    @Override
    public Object fromString(String dtz) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("dd/MM/dd");
            Date date = formatter.parse(dtz);
            LocalDateTime localDateTime = DateUtils.convertFromDate(date);
            return localDateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
