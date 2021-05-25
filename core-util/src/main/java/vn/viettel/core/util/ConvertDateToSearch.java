package vn.viettel.core.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class ConvertDateToSearch {

    public static Timestamp convertFromDate(Date sFromDate)
    {
        if( sFromDate == null) return null;
        return new Timestamp(sFromDate.getTime());

    }

    public static Timestamp convertToDate( Date sToDate)
    {
        if(sToDate == null) return null;
        LocalDateTime localDateTime = LocalDateTime
                .of(sToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
        return Timestamp.valueOf(localDateTime);
    }

}
