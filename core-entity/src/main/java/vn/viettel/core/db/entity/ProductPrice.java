package vn.viettel.core.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(name = "STATUS")
    private Integer status;

}
