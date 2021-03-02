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
@Table(name = "product_price")
public class ProductPrice extends BaseEntity {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "price")
    private Float price;

    @Column(name = "price_not_tax")
    private Float priceNotTax;

    @Column(name = "package_price")
    private Float packagePrice;

    @Column(name = "package_price_not_tax")
    private Float packagePriceNotTax;

    @Column(name = "from_date")
    private LocalDateTime fromDate;

    @Column(name = "to_date")
    private LocalDateTime toDate;

    @Column(name = "status")
    private Integer status;

}
