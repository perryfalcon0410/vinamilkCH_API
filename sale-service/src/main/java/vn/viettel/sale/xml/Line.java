package vn.viettel.sale.xml;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.sale.entities.PoConfirm;
import vn.viettel.sale.entities.PoDetail;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Line {
    private String DistCode;
    private String PONumber;
    private String POCONumber;
    private String ItemCode;
    private String ItemDescr;
    private String UOM;
    private String SiteID;
    private String LOT_NUMBER;
    private LocalDateTime EXPIRE_DATE;
    private Integer Quantity;
    private Double Price;
    private Double LineTotal;
    private String SaleOrderNumber;
}
