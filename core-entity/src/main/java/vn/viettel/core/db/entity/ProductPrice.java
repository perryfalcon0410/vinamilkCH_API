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
@Table(name = "productprice")
public class ProductPrice extends BaseEntity {

    @Column(name = "product_id")
    private Long product_id;

    @Column(name = "price")
    private Float price;

    @Column(name = "pricenottax")
    private Float pricenottax;

    @Column(name = "packageprice")
    private Float packageprice;

    @Column(name = "packagepricenottax")
    private Float packagepricenottax;

    @Column(name = "fromdate")
    private LocalDateTime fromdate;

    @Column(name = "todate")
    private LocalDateTime todate;

    @Column(name = "status")
    private Integer status;

}
