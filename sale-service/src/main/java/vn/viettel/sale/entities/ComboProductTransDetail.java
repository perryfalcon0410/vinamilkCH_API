package vn.viettel.sale.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.viettel.core.db.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMBO_PRODUCT_TRANS_DETAIL")
public class ComboProductTransDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "TRANS_ID")
    private Long transId;
    @Column(name = "SHOP_ID")
    private Long shopId;
    @Column(name = " TRANS_CODE")
    private String transCode;
    @Column(name = "TRANS_DATE")
    private LocalDateTime transDate;
    @Column(name = "COMBO_PRODUCT_ID")
    private Long comboProductId;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "IS_COMBO")
    private Integer isCombo;
    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "PRICE_NOT_VAT")
    private Double priceNotVat;
    @Column(name = "AMOUNT")
    private Double amount;

}
