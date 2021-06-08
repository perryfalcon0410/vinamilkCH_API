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
@XStreamAlias("Line")
public class Line {
    @XStreamAlias("DistCode")
    private String distCode;
    @XStreamAlias("PONumber")
    private String PONumber;
    @XStreamAlias("POCONumber")
    private String poCoNumber;
    @XStreamAlias("ItemCode")
    private String itemCode;
    @XStreamAlias("ItemDescr")
    private String itemDescr;
    @XStreamAlias("UOM")
    private String uom;
    @XStreamAlias("SiteID")
    private String siteId;
    @XStreamAlias("LOT_NUMBER")
    private String lotNumber;
    @XStreamAlias("EXPIRE_DATE")
    @XStreamConverter(DTZConverter.class)
    private LocalDateTime expireDate;
    @XStreamAlias("Quantity")
    private Integer quantity;
    @XStreamAlias("Price")
    private Double price;
    @XStreamAlias("VAT")
    private Double vat;
    @XStreamAlias("LineTotal")
    private Double lineTotal;
    @XStreamAlias("SaleOrderNumber")
    private String saleOrderNumber;
}
