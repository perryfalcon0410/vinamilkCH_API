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
@Table(name = "PRICES")
public class Price extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "PRICE_TYPE")
    private Integer priceType;
    @Column(name = "CUSTOMER_TYPE_ID")
    private Long customerTypeId;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "PRICE_NOT_VAT")
    private Double priceNotVat;
    @Column(name = "FROM_DATE")
    private Date fromDate;
    @Column(name = "VAT")
    private Double vat;
    @Column(name = "TO_DATE")
    private Date toDate;
    @Column(name = "STATUS")
    private Integer status;
}
