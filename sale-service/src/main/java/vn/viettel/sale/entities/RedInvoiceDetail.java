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
@Table(name = "RED_INVOICE_DETAILS")
public class RedInvoiceDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
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
