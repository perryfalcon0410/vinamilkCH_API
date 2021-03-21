package vn.viettel.core.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PRODUCT_PRICES")
public class ProductPrice extends BaseEntity {

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "PRICE")
    private Float price;

    @Column(name = "PRICE_NOT_TAX")
    private Float priceNotTax;

    @Column(name = "PACKAGE_PRICE")
    private Float packagePrice;

    @Column(name = "PACKAGE_PRICE_NOT_TAX")
    private Float packagePriceNotTax;

    @Column(name = "FROM_DATE")
    private LocalDateTime fromDate;

    @Column(name = "TO_DATE")
    private LocalDateTime toDate;

}
