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
@Table(name = "PO_TRANS_DETAIL")
public class PoTransDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "TRANS_ID")
    private Long transId;
    @Column(name = "TRANS_DATE")
    private Date transDate;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "AMOUNT_NOT_VAT")
    private Float amountNotVat;
    @Column(name = "RETURN_AMOUNT")
    private Integer returnAmount;
}
