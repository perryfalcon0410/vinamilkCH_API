package vn.viettel.core.convert;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import vn.viettel.core.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DTZConverterV3 extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        return type.equals(LocalDateTime.class);
    }

    @Override
    public Object fromString(String dtz) {
        if(dtz == null || dtz.isEmpty()) return null;
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MMM-dd"); //2022-JAN-06
            Date date = formatter.parse(dtz);
            LocalDateTime localDateTime = DateUtils.convertFromDate(date);
            return localDateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
