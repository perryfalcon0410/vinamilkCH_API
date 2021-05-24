package vn.viettel.core.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class ConvertDateToSearch {

    public static Timestamp convertFromDate(Date sFromDate, Date sToDate)
    {
        Timestamp tsFromDate = null;
        if(sFromDate != null){
            tsFromDate =new Timestamp(sFromDate.getTime());
        }else if(sToDate!=null){
            LocalDateTime localDateTime = LocalDateTime
                    .of(sToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
            tsFromDate= Timestamp.valueOf(localDateTime);
        }
        return tsFromDate;
    }

    public static Timestamp convertToDate(Date sFromDate, Date sToDate)
    {
        Timestamp tsToDate = null;
        if(sToDate != null)
        {
            LocalDateTime localDateTime = LocalDateTime
                    .of(sToDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MAX);
            tsToDate= Timestamp.valueOf(localDateTime);
        }else if(sFromDate != null){
            tsToDate =new Timestamp(sFromDate.getTime());
        }
        return tsToDate;
    }

}
