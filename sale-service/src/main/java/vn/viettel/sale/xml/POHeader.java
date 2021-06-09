package vn.viettel.sale.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.convert.DTZConverter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("POHeader")
public class POHeader {
    @XStreamAlias("DistCode")
    private String distCode;
    @XStreamAlias("PONumber")
    private String poNumber;
    @XStreamAlias("POCONumber")
    private String poCoNumber;
    @XStreamAlias("SO_HD")
    private String soHd;
    @XStreamAlias("KEY_CODE")
    private String keyCode;
    @XStreamAlias("BillToLocation")
    private String billToLocation;
    @XStreamAlias("ShipToLocation")
    private String shipToLocation;
    @XStreamAlias("OrderDate")
    @XStreamConverter(DTZConverter.class)
    private LocalDateTime orderDate;
    @XStreamAlias("RequestDate")
    @XStreamConverter(DTZConverter.class)
    private LocalDateTime requestDate;
    @XStreamAlias("Status")
    private String status;
    @XStreamAlias("Total")
    private Float total;
}
