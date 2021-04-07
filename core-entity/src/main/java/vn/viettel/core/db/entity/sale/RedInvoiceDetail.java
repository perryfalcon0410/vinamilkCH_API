package vn.viettel.core.db.entity.sale;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RED_INVOICE_DETAILS")
public class RedInvoiceDetail extends BaseEntity {
    @Column(name = "RED_INVOICE_ID")
    private Long redInvoiceId;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = "PRINT_DATE")
    private Date printDate;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Float price;
    @Column(name = "PRICE_NOT_VAT")
    private Float priceNotVat;
    @Column(name = "AMOUNT_NOT_VAT")
    private Float amountNotVat;
    @Column(name = "AMOUNT")
    private Float amount;
    @Column(name = "NOTE")
    private String note;
}
