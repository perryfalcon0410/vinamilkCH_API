package vn.viettel.sale.xml;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class POHeader {
    private String DistCode;
    private String PONumber;
    private String POCONumber;
    private String SO_HD;
    private String KEY_CODE;
    private String BillToLocation;
    private String ShipToLocation;
    private LocalDateTime OrderDate;
    private LocalDateTime RequestDate;
    private String Status;
    private Float Total;
}
