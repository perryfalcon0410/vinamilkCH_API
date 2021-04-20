package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ONLINE_ORDER_DETAIL")
public class OnlineOrderDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "ONLINE_ORDER_ID")
    private Long onlineOrderId;
    @Column(name = "CREATE_DATE")
    private Date createDate;
    @Column(name = "SKU")
    private String sku;
    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "ORIGINAL_PRICE")
    private Float originalPrice;
    @Column(name = "RETAILS_PRICE")
    private Float retailsPrice;
    @Column(name = "LINE_VALUE")
    private Float lineValue;
    @Column(name = "CHARACTER1_NAME")
    private String character1Name;
    @Column(name = "CHARACTER1_VALUE")
    private String character1Value;
    @Column(name = "CHARACTER2_NAME")
    private String character2Name;
    @Column(name = "CHARACTER2_VALUE")
    private String character2Value;
    @Column(name = "CHARACTER3_NAME")
    private String character3Name;
    @Column(name = "CHARACTER3_VALUE")
    private String character3Value;
    @Column(name = "PROMOTION_NAME")
    private String promotionName;
}
