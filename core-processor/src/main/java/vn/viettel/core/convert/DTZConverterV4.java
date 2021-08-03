package vn.viettel.core.convert;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import vn.viettel.core.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DTZConverterV4 extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        return type.equals(LocalDateTime.class);
    }

    @Override
    public Object fromString(String dtz) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date date = formatter.parse(dtz);
            LocalDateTime localDateTime = DateUtils.convertDateToLocalDateTime(date);
            return localDateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
