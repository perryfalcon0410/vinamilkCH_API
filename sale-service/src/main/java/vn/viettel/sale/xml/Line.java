package vn.viettel.sale.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.convert.DTZConverter;
import vn.viettel.core.convert.DoubleConverter;
import vn.viettel.core.convert.FloatConverter;
import vn.viettel.sale.entities.OnlineOrderDetail;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Line")
public class Line {
    //Common
    @XStreamAlias("UOM")
    private String uom;
    @XStreamAlias("Quantity")
    private Integer quantity;

    //Po
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
    @XStreamAlias("SiteID")
    private String siteId;
    @XStreamAlias("LOT_NUMBER")
    private String lotNumber;
    @XStreamAlias("EXPIRE_DATE")
    @XStreamConverter(DTZConverter.class)
    private LocalDateTime expireDate;
    @XStreamAlias("Price")
    @XStreamConverter(DoubleConverter.class)
    private Double price;
    @XStreamAlias("VAT")
    @XStreamConverter(DoubleConverter.class)
    private Double vat;
    @XStreamAlias("LineTotal")
    @XStreamConverter(DoubleConverter.class)
    private Double lineTotal;
    @XStreamAlias("SaleOrderNumber")
    private String saleOrderNumber;

    //Online order
    @XStreamAlias("Sku")
    private String sku;
    @XStreamAlias("ProductName")
    private String productName;
    @XStreamAlias("Character1Name")
    private String character1Name;
    @XStreamAlias("Character1Value")
    private String character1Value;
    @XStreamAlias("Character2Name")
    private String character2Name;
    @XStreamAlias("Character2Value")
    private String character2Value;
    @XStreamAlias("Character3Name")
    private String character3Name;
    @XStreamAlias("Character3Value")
    private String character3Value;
    @XStreamAlias("OriginalPrice")
    @XStreamConverter(FloatConverter.class)
    private Float originalPrice;
    @XStreamAlias("RetailsPrice")
    @XStreamConverter(FloatConverter.class)
    private Float retailsPrice;
    @XStreamAlias("LineValue")
    @XStreamConverter(FloatConverter.class)
    private Float lineValue;
    @XStreamAlias("PromotionName")
    private String promotionName;

}
